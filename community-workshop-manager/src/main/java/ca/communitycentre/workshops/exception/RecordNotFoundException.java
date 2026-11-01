package ca.communitycentre.workshops.exception;

/** Raised when a URL or form refers to an instructor or workshop ID that does not exist. */
public class RecordNotFoundException extends RuntimeException {

    public RecordNotFoundException(String recordType, Long id) {
        super(recordType + " with ID " + id + " was not found.");
    }
}
