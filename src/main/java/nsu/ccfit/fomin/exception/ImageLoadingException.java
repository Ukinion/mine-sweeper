package nsu.ccfit.fomin.exception;

public class ImageLoadingException extends GameException {
    public ImageLoadingException(String whichImageFailed) {
        super(whichImageFailed + " image loading failed!");
    }
}
