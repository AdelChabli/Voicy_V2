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
    public static final String OUTPUT_PHONE= Environment.getExternalStorageDirectory() + "/Voicy/Phonemes";
    public static final String OUTPUT_SENTENCE = Environment.getExternalStorageDirectory() + "/Voicy/Phrases";

    public void initProject()
    {
        createInternalDirectory();
        createFolderInAppFolder("Phonemes");
        createFolderInAppFolder("Phrases");
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

    // Permet de supprimer un dossier et son contenu dans le dossier de l'application
    public void rmdirFolderInAppFolder(String directoryName)
    {
        File file = new File(OUTPUT_DIRECTORY + "/" + directoryName);
        if (file.isDirectory())
        {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(file, children[i]).delete();
            }
        }
    }

    // Permet de supprimer tous les dossiers de l'application
    public void rmdirAllAppFolder()
    {
        File file = new File(OUTPUT_DIRECTORY);
        if (file.isDirectory())
        {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++)
            {
                if(file.isDirectory())
                    rmdirFolderInAppFolder(file.getName());
                else
                    file.delete();
            }
        }
    }
    public File getFileTest(String sFile) {
        return new File(OUTPUT_DIRECTORY+"/"+sFile);
    }
}
