package org.superbiz.moviefun.albums;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.blob.Blob;
import org.superbiz.moviefun.blob.BlobStore;
import org.superbiz.moviefun.blob.FileStore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final AlbumsBean albumsBean;
    private BlobStore blobStore;

    public AlbumsController(AlbumsBean albumsBean, BlobStore blobStore) {
        this.albumsBean = albumsBean;
        this.blobStore = blobStore;
    }

    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        Blob blob = new Blob(Long.toString(albumId),
                uploadedFile.getInputStream(),
                uploadedFile.getContentType());
        blobStore.put(blob);

        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException {

        Optional<Blob> optionalBlob = blobStore.get(Long.toString(albumId));
        if (optionalBlob.isPresent()) {
            Blob blob = optionalBlob.get();
            byte[] imageBytes = IOUtils.toByteArray(blob.getInputStream());
            HttpHeaders headers = createImageHttpHeaders(blob.getContentType(), imageBytes);
            return new HttpEntity<>(imageBytes, headers);
        } else {
            InputStream defaultImageInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("/default-cover.jpg");
            byte[] imagesBytes = IOUtils.toByteArray(defaultImageInputStream);
            return new HttpEntity<>(imagesBytes, createImageHttpHeaders(new Tika().detect(defaultImageInputStream), imagesBytes));
        }
    }

    private HttpHeaders createImageHttpHeaders(String contentType, byte[] imageBytes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(imageBytes.length);
        return headers;
    }

}
