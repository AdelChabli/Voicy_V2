package com.example.voicy_v2.model;

import android.os.Environment;

import java.io.File;

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

    public void initProject()
    {
        createInternalDirectory();
        createFolderInAppFolder("Logatomes");
        createFolderInAppFolder("Phrases");
        createFolderInAppFolder("Resultats");
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

        file.delete();
    }
    public File getFileTest(String sFile) {
        return new File(OUTPUT_DIRECTORY+"/"+sFile);
    } // TODO à virer ?
}
