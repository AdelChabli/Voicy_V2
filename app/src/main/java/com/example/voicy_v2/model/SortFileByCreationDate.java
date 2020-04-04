package com.example.voicy_v2.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class SortFileByCreationDate
{
    // -------------------- SINGLETON ---------------------------
    private SortFileByCreationDate() {}

    private static SortFileByCreationDate INSTANCE = null;

    public static synchronized SortFileByCreationDate getInstance()
    {
        if (INSTANCE == null) { INSTANCE = new SortFileByCreationDate(); }
        return INSTANCE;
    }
    // -----------------------------------------------------------

    public File[] getListSorted(String pathDirectory)
    {
        File dir = new File(pathDirectory);

        File[] dirs = dir.listFiles();

        return sortDirsByDateCreated(dirs);
    }

    private File[] sortDirsByDateCreated(File[] dirs)
    {
        Arrays.sort(dirs, new Comparator<File>()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public int compare(File dir1, File dir2) {
                long d1 = getDirCreationDate(dir1);
                long d2 = getDirCreationDate(dir2);

                return Long.valueOf(d1).compareTo(d2);
            }
        });

        return dirs;
    }

    private long getDirCreationDate(File dir)
    {
        return dir.lastModified();
    }
}
