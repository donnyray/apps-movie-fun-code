package org.superbiz.moviefun.blob;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.Optional;

import static java.lang.String.format;

public class FileStore implements BlobStore {

    @Override
    public void put(Blob blob) throws IOException {

        String coverFileName = format("covers/%s", blob.getName());
        File targetFile = new File(coverFileName);
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();
        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            outputStream.write(IOUtils.toByteArray(blob.getInputStream()));
        }

    }

    @Override
    public Optional<Blob> get(String name) throws IOException {

        String coverFileName = format("covers/%s", name);
        File file = new File(coverFileName);
        byte[] bytes = Files.readAllBytes(file.toPath());

        return Optional.of(new Blob(coverFileName,
                new ByteArrayInputStream(bytes),
                new Tika().detect( new File(format("covers/%s", name)))));
    }

    @Override
    public void deleteAll() {

    }
}
