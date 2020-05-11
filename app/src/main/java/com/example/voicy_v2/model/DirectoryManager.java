package com.example.voicy_v2.model;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class DirectoryManager
{

    // -------------------- SINGLETON ---------------------------
    private DirectoryManager() {}

    private static DirectoryManager INSTANCE = null;

    public static synchronized DirectoryManager getInstance()
    {
        if (INSTANCE == null) { INSTANCE = new DirectoryManager(); }
        return INSTANCE;
    }
    // -----------------------------------------------------------

    // Arguments
    public static final String OUTPUT_DIRECTORY = Environment.getExternalStorageDirectory() + "/Voicy";
    public static final String OUTPUT_PHONE= Environment.getExternalStorageDirectory() + "/Voicy/Logatomes";
    public static final String OUTPUT_SENTENCE = Environment.getExternalStorageDirectory() + "/Voicy/Phrases";
    public static final String OUTPUT_RESULTAT = Environment.getExternalStorageDirectory() + "/Voicy/Resultats";
    public static final String OUTPUT_ATTENTE = Environment.getExternalStorageDirectory() + "/Voicy/Attente";

    public void initProject()
    {
        createInternalDirectory();
        createFolderInAppFolder("Logatomes");
        createFolderInAppFolder("Phrases");
        createFolderInAppFolder("Resultats");
        createFolderInAppFolder("Attente");
    }

    public void createFolder(String path)
    {
        File file = new File(path);

        if (!file.exists())
        {
            if(file.mkdir())
            {
                LogVoicy.getInstance().createLogInfo("Création du dossier " + path);
            }
            else
            {
                LogVoicy.getInstance().createLogError("Impossible de créer le dossier mkdir fail " + path);
            }
        }
        else
        {
            LogVoicy.getInstance().createLogError("Impossible de créer le dossier, le dossier existe déjà : " + path);
        }

    }

    // Créer le dossier de notre application sur l'appareil Android
    private void createInternalDirectory()
    {
        File file = new File(OUTPUT_DIRECTORY);
        if (!file.exists())
            file.mkdir();
    }

    // Créer un dossier à l'intérieur du dossier de l'application
    public void createFolderInAppFolder(String directoryName)
    {
        File file = new File(OUTPUT_DIRECTORY + "/" + directoryName);
        if (!file.exists())
            file.mkdir();
    }

    public void rmdirFolder(String path)
    {
        File file = new File(path);
        if (file.isDirectory())
        {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(file, children[i]).delete();
            }
        }

        if(file.delete())
        {
            LogVoicy.getInstance().createLogInfo("Suppression de " + path);
        }
        else
        {
            LogVoicy.getInstance().createLogError("Impossible de supprimer " + path);
        }
    }

    public void cutAndPastFolderToAnother(String pathFolder, String pathDestination)
    {
        String folderName = pathFolder.substring(pathFolder.lastIndexOf("/") + 1, pathFolder.length());

        String folderDestination = pathDestination + "/" + folderName;
        createFolder(folderDestination);

        File fileSource = new File(pathFolder);

        List<File> listFile = new ArrayList<>();

        if(fileSource.isDirectory())
        {
            String[] children = fileSource.list();

            for(int i = 0; i < children.length; i++)
            {
                listFile.add(new File(fileSource, children[i]));
            }
        }

        for(File file : listFile)
        {
            copyFileToAnotherDir(file, folderDestination);
        }

        rmdirFolder(pathFolder);
    }

    public void copyFileToAnotherDir(File fileSource, String destination)
    {
        if(fileSource.renameTo(new File(destination+"/"+fileSource.getName())))
        {
            LogVoicy.getInstance().createLogInfo("Copy de " + fileSource.getName() + " vers " + destination + " réussi !");
        }
        else
        {
            LogVoicy.getInstance().createLogError("Copy de " + fileSource.getName() + " vers " + destination + " échoué !");
        }
    }

    public void createFileOnDirectory(String pathDirectory, String nameFile, String data)
    {
        try {
            File file = new File(pathDirectory + "/" + nameFile);
            FileWriter writer = new FileWriter(file);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getAvailableMo()
    {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());

        long bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        long megAvailable   = bytesAvailable / 1048576;

        LogVoicy.getInstance().createLogInfo("Mo disponible = " + (int)megAvailable);

        return (int)megAvailable;
    }


    public File getFileTest(String sFile) {
        return new File(OUTPUT_DIRECTORY+"/"+sFile);
    } // TODO à virer ?
}
