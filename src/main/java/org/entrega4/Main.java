package org.entrega4;

import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

public class Main {
    static Scanner scNum = new Scanner(System.in);
    static Scanner scStr = new Scanner(System.in);
    static MongoClient mc;
    static MongoDatabase mdb;
    static MongoCollection mco;
    static int cont = 0;

    private static int checkItems(){
        int num = 0;
        Document sort = new Document("_id",-1);
        FindIterable itDoc = mco.find().limit(1).sort(sort).projection(Projections.exclude("nombre","estudio","anio"));
        Iterator it = itDoc.iterator();
        while(it.hasNext()){
            sort = (Document)it.next();
             num = sort.getInteger("_id");
        }
        return num;
    }
    public static void main(String[] args) {
        mc = new MongoClient();
        mdb = mc.getDatabase("videojuegos");
        mco = mdb.getCollection("videojuego");
        cont = checkItems();
        /*mc = new MongoClient();
        mdb = mc.getDatabase("videojuegos");
        mco = mdb.getCollection("videojuego");*/
        int menu;
        showBanner();
        while(true){
            askMenu();
            menu = askNumber('m');
            switch (menu) {
                case 1 -> {
                    alta();
                }
                case 2 -> {
                    edit();
                }
                case 3 -> {
                    delete();
                }
                case 4 -> {
                    search();
                }
                case 5 -> {
                    specialedit();
                }
                case 0 -> {
                    mc.close();
                    System.exit(0);
                }
                default -> {
                }
            }
        }
    }

    private static void showBanner(){
        System.out.println("8888888888          888                                                    888     888 88888888888            d8888");
        System.out.println("888                 888                                                    888     888     888               d8P888");
        System.out.println("888                 888                                                    888     888     888              d8P 888");
        System.out.println("8888888    88888b.  888888 888d888  .d88b.   .d88b.   8888b.  .d8888b      888     888     888             d8P  888");
        System.out.println("888        888 \"88b 888    888P\"   d8P  Y8b d88P\"88b     \"88b 88K          888     888     888            d88   888");
        System.out.println("888        888  888 888    888     88888888 888  888 .d888888 \"Y8888b.     888     888     888     888888 8888888888");
        System.out.println("888        888  888 Y88b.  888     Y8b.     Y88b 888 888  888      X88     Y88b. .d88P     888                  888");
        System.out.println("8888888888 888  888  \"Y888 888      \"Y8888   \"Y88888 \"Y888888  88888P'      \"Y88888P\"      888                  888");
        System.out.println("                                                 888");
        System.out.println("                                            Y8b d88P");
        System.out.println("                                             \"Y88P\"");
        System.out.println("####################################################################################################################");
    }

    private static void askMenu(){
        System.out.println("Elige una opción:\n" +
                "1.- Añadir Videojuego\n" +
                "2.- Editar Videojuego\n" +
                "3.- Eliminar Videojuego\n" +
                "4.- Acceder al Motor de Búsqueda\n" +
                "5.- Edición especial\n" +
                "0.- Salir");
    }

    private static void delete(){
        if(checkItems() == 0){
            System.out.println("No hay videojuegos para eliminar, introduce uno primero.");
        }
        else{
            muestraVideojuegos("_id");
            System.out.println("¿Qué videojuego quieres borrar? [0] Para salir");
            int delete = askNumber('d');
            if(checkIfExists(delete)){
                mco.deleteOne(Filters.eq("_id",delete));
                System.out.println("Borrado satisfactoriamente.");
            }
            else{
                System.out.println("NO EXISTE ESE ID, NO ME HAGAS PERDER EL TIEMPO, BRIBÓN");
            }
        }
    }
    private static void specialedit(){
        System.out.println("""
                Ediciones especiales:
                1.- Sumar 1 año al lanzamiento de todos los videojuegos
                2.- Restar 2 años al lanzamiento de los videojuegos lanzados antes del año 2000""");
        int menu = askNumber('m');
        switch(menu){
            case 1 ->{
                Document filtro = new Document();
                mco.updateMany(filtro,Updates.inc("anio",1));
            }
            case 2 ->{
                Document filtro = new Document("anio",new Document("$lte",1990));
                mco.updateMany(filtro,Updates.inc("anio",-2));
            }
        }
    }
    private static void search(){
        System.out.println("""
                Motor de Búsqueda:
                1.- Mostrar todos los videojuegos ordenados por ID
                2.- Mostrar todos los videojuegos ordenados por Año de lanzamiento
                3.- Mostrar todos los videojuegos lanzados después del año 2000
                4.- Mostrar el videojuego más antiguo y el más nuevo
                5.- Mostrar la media de años de los juegos en la base de datos""");
        int menu = askNumber('m');
        switch(menu){
            case 1 -> {
                muestraVideojuegos("_id");
            }
            case 2 -> {
                muestraVideojuegos("anio");
            }
            case 3 -> {
                Document filtro = new Document("anio",new Document("$gt",2000));
                FindIterable docIt = mco.find(filtro);
                Iterator it = docIt.iterator();
                int id,anio;
                String nombre,estudio;
                System.out.println("Lista de videojuegos: ");
                for(int i = 1;it.hasNext();i++){
                    Document videojuego = (Document)it.next();
                    id = videojuego.getInteger("_id");
                    nombre = videojuego.getString("nombre");
                    estudio = videojuego.getString("estudio");
                    anio = videojuego.getInteger("anio");
                    System.out.println("Videojuego "+i+":" +
                            "\nID: " +id+
                            "\nNombre: "+nombre+
                            "\nEstudio: "+estudio+
                            "\nAño: "+anio+"\n");
                }
            }
            case 4 -> {
                AggregateIterable<Document> result = mco.aggregate(List.of(
                        Aggregates.group(null,
                                Accumulators.max("maxAnio", "$anio"),
                                Accumulators.min("minAnio","$anio")
                        )
                ));
                Iterator it = result.iterator();
                while(it.hasNext()){
                    Document doc = (Document)it.next();
                    System.out.println("El juevo más nuevo fué lanzado en el año: "+doc.getInteger("maxAnio")+" y el más antiguo el año: "+doc.getInteger("minAnio"));
                }
            }
            case 5 -> {
                AggregateIterable<Document> result = mco.aggregate(Arrays.asList(
                        Aggregates.project(Projections.fields(
                                Projections.computed("tiempo_lanzado", new Document("$subtract", Arrays.asList(2023, "$anio")))
                        )),
                        Aggregates.group(null,
                                Accumulators.avg("edadPromedio", "$tiempo_lanzado")
                        )
                ));
                Iterator it = result.iterator();
                while(it.hasNext()){
                    Document doc = (Document)it.next();
                    System.out.println("La edad promedio de los juegos introducidos es: "+doc.getDouble("edadPromedio"));
                }
            }
            default -> {
                System.out.println("Vale, veo que no quieres hacer nada, no pasa nada, nos veremos las caras en otro momento.");
            }
        }
    }

    private static boolean checkIfExists(int find){
        boolean exists = true;
        long count = mco.countDocuments(new Document("_id",find));
        if (count == 0){
            return !exists;
        }
        else{
            return exists;
        }
    }
    private static int askNumber(char var){
        int num = -1;
        while(num <1){
            try {
                num = scNum.nextInt();
                if (num < 0) {
                    System.out.println("Por favor, introduce un número dentro de lo razonable ;)");
                }
                if (num == 0){
                    System.out.println("Vale, veo que no quieres hacer nada, no pasa nada, nos veremos las caras en otro momento.");
                    break;
                }
            }
            catch(Exception e){
                switch(var){
                    case 'a':{
                        System.out.println("DIJE... ¿CUÁNTAS ALTAS QUIERES HACER?");
                        break;
                    }
                    case 'm':{
                        System.out.println("DIJE... ¿QUÉ OPCIÓN DEL MENÚ QUIERES?");
                        break;
                    }
                    case 'e':{
                        System.out.println("PERO A VER... ¿QUÉ QUIERES MODIFICAR?");
                    }
                    case 'd':{
                        System.out.println("PERO A VER... ¿QUÉ QUIERES BORRAR?");
                    }
                }
                scNum.nextLine();
            }
        }
        return num;
    }

    private static int askAltas(){
        System.out.println("¿Cuántas altas quieres hacer? [0] Para salir");
        return askNumber('a');
    }

    private static Document createVideojuego(){
        cont++;
        String nombre = checkStr('t');
        String estudio = checkStr('e');
        int anio = checkInt();
        Videojuego v1 = new Videojuego(cont,nombre,estudio,anio);
        return new Document("_id",v1.getId())
                .append("nombre",v1.getNombre())
                .append("estudio",v1.getEstudio())
                .append("anio",v1.getAnio());
    }
    private static String checkStr(char opc){
        String str = "";
        boolean check = true;
        while(check){
            try {
                switch (opc) {
                    case 't' -> {
                        System.out.println("Por favor, introduce el título del videojuego");
                    }
                    case 'e' -> {
                        System.out.println("Por favor, introduce el estudio desarrollador del videojuego");
                    }
                }
                str = scStr.nextLine();
                if (str.length()>0){
                    check = false;
                }
            }
            catch(Exception e){
                switch (opc) {
                    case 't' -> {
                        System.out.println("Por favor, introduce un título dentro de lo razonable ;)");
                    }
                    case 'e' -> {
                        System.out.println("Por favor, introduce un estudio dentro de lo razonable ;)");
                    }
                }

            }
        }
        return str;
    }
    private static int checkInt(){
        int num = 0;
        while(num <1){
            try {
                System.out.println("Por favor, introduce el año de lanzamiento del videojuego");
                num = scNum.nextInt();
                if (num < 1) {
                    System.out.println("Por favor, introduce un año dentro de lo razonable ;)");
                }
            }
            catch(Exception e){
                System.out.println("Por favor, introduce un año dentro de lo razonable ;)");
                scNum.nextLine();
            }
        }
        return num;

    }
    private static void alta(){

        int num = askAltas();

        if (num > 1){
            List<Document> videojuegos = new ArrayList<>();
            for(int i = 0; i< num;i++){
                System.out.println("Videojuego "+(i+1)+": ");
                videojuegos.add(createVideojuego());
            }
            mco.insertMany(videojuegos);
            System.out.println("¡Videojuegos Añadidos!");
        }
        if (num == 1){
            mco.insertOne(createVideojuego());
            System.out.println("¡Videojuego Añadido!");

        }
    }

    private static void muestraVideojuegos(String sort){
        Document filtro = new Document();
        FindIterable docIt = mco.find(filtro).sort(Sorts.ascending(sort));
        Iterator it = docIt.iterator();
        int id,anio;
        String nombre,estudio;
        System.out.println("Lista de videojuegos: ");
        for(int i = 1;it.hasNext();i++){
            Document videojuego = (Document)it.next();
            id = videojuego.getInteger("_id");
            nombre = videojuego.getString("nombre");
            estudio = videojuego.getString("estudio");
            anio = videojuego.getInteger("anio");
            System.out.println("Videojuego "+i+":" +
                    "\nID: " +id+
                    "\nNombre: "+nombre+
                    "\nEstudio: "+estudio+
                    "\nAño: "+anio+"\n");
        }
        /*while(it.hasNext()){
            Document videojuego = (Document)it.next();
            Videojuego v1 = new Videojuego(videojuego.getInteger("_id"), videojuego.getString("nombre"),videojuego.getString("estudio"),videojuego.getInteger("anio"));
            System.out.println(v1);
        }*/
    }
    private static void edit(){
        if(checkItems() == 0){
            System.out.println("No hay videojuegos para editar, introduce uno primero.");
        }
        else{
            boolean continuasion = true;
            muestraVideojuegos("_id");
            System.out.println("¿Qué videojuego quieres editar? [0] Para salir");
            int edit = askNumber('e');
            if(edit != 0){
                Document search = new Document("_id",edit);
                FindIterable fiDoc = mco.find(search);
                Iterator it = fiDoc.iterator();
                Document videojuego = (Document)it.next();
                Videojuego v1 = new Videojuego(videojuego.getInteger("_id"), videojuego.getString("nombre"),videojuego.getString("estudio"),videojuego.getInteger("anio"));
                System.out.println(v1);
                while(continuasion){
                    System.out.println("¿Qué quieres editar? [nombre],[estudio],[año],[salir]");
                    char ask = scStr.nextLine().toUpperCase().charAt(0);
                    switch(ask){
                        case 'N':{
                            String nombre = checkStr('t');
                            mco.updateOne(search, Updates.set("nombre",nombre));
                            break;
                        }
                        case 'E':{
                            String estudio = checkStr('e');
                            mco.updateOne(search, Updates.set("estudio",estudio));
                            break;
                        }
                        case 'A':{
                            int anio = checkInt();
                            mco.updateOne(search, Updates.set("anio",anio));
                            break;
                        }
                        case 'S':{
                            continuasion = false;
                        }
                        default: {
                            System.out.println();
                            break;
                        }
                    }
                }
            }
            else{
                System.out.println("Vale, veo que no quieres hacer nada, no pasa nada, nos veremos las caras en otro momento.");
            }
        }
    }
}
