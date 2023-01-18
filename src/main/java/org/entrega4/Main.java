package org.entrega4;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

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
            switch(menu){
                case 1:{
                    alta();
                    break;
                }
                case 2:{
                    edit();
                    break;
                }
                case 3:{
                    delete();
                    break;
                }
                case 4:{
                    //search();
                    break;
                }
                case 0:{
                    mc.close();
                    System.exit(0);
                }
                default:{
                    break;
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
                "3.- Eliminar Videojuego");
    }

    private static void delete(){
        if(checkItems() == 0){
            System.out.println("No hay videojuegos para eliminar, introduce uno primero.");
        }
        else{
            muestraVideojuegos();
            System.out.println("¿Qué videojuego quieres borrar?");
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
        System.out.println("¿Cuántas altas quieres hacer?");
        return askNumber('a');
    }

    private static Document createVideojuego(){
        cont++;
        String nombre = checkStr('t');
        String estudio = checkStr('e');
        int anio = checkInt();
        Videojuego v1 = new Videojuego(cont,nombre,estudio,anio);
        Document videojuego = new Document("_id",v1.getId())
                .append("nombre",v1.getNombre())
                .append("estudio",v1.getEstudio())
                .append("anio",v1.getAnio());
        return videojuego;
    }
    private static String checkStr(char opc){
        String str = "";
        boolean check = true;
        while(check){
            try {
                switch(opc){
                    case 't':{
                        System.out.println("Por favor, introduce el título del videojuego");
                        break;
                    }
                    case 'e':{
                        System.out.println("Por favor, introduce el estudio desarrollador del videojuego");
                        break;
                    }
                }
                str = scStr.nextLine();
                if (str.length()>0){
                    check = false;
                }
            }
            catch(Exception e){
                switch(opc){
                    case 't':{
                        System.out.println("Por favor, introduce un título dentro de lo razonable ;)");
                        break;
                    }
                    case 'e':{
                        System.out.println("Por favor, introduce un estudio dentro de lo razonable ;)");
                        break;
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

    private static void muestraVideojuegos(){
        Document filtro = new Document();
        FindIterable docIt = mco.find(filtro);
        Iterator it = docIt.iterator();
        int id,anio;
        String nombre,estudio;
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
            muestraVideojuegos();
            System.out.println("¿Qué videojuego quieres editar?");
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
