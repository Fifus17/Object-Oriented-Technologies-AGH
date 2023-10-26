package app;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import model.Photo;
import model.PhotoSize;
import util.PhotoDownloader;
import util.PhotoProcessor;
import util.PhotoSerializer;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static model.PhotoSize.resolve;

public class PhotoCrawler {

    private static final Logger log = Logger.getLogger(PhotoCrawler.class.getName());

    private final PhotoDownloader photoDownloader;

    private final PhotoSerializer photoSerializer;

    private final PhotoProcessor photoProcessor;

    public PhotoCrawler() throws IOException {
        this.photoDownloader = new PhotoDownloader();
        this.photoSerializer = new PhotoSerializer("./photos");
        this.photoProcessor = new PhotoProcessor();
    }

    public void resetLibrary() throws IOException {
        photoSerializer.deleteLibraryContents();
    }

    public void downloadPhotoExamples() {
        try {
            photoDownloader.getPhotoExamples().subscribe(photoSerializer::savePhoto);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Downloading photo examples error", e);
        }
    }

    public void downloadPhotosForQuery(String query) {
        photoDownloader.searchForPhotos(query).compose(this::processPhotos).subscribe(photoSerializer::savePhoto, error -> log.log(Level.SEVERE, "Searching photos error", error));
    }

    public void downloadPhotosForMultipleQueries(List<String> queries) {
        photoDownloader.searchForPhotos(queries).compose(this::processPhotos).subscribeOn(Schedulers.io()).subscribe(photoSerializer::savePhoto, error -> log.log(Level.SEVERE, "Searching photos error", error));
    }

    public Observable<Photo> processPhotos(Observable<Photo> photos) {
        return photos.filter(photo -> resolve(photo) != PhotoSize.SMALL).map(photoProcessor::convertToMiniature);
    }
}
