package kirya.view;

import javafx.event.Event;
import javafx.event.EventType;
import kirya.utils.DisplayableStudyGuide;

/**
 * StudyGuide custom event
 */
public class StudyGuideEvent extends Event {
    /**
     * Event to download.
     */
    public static final EventType<StudyGuideEvent> DOWNLOAD = new EventType<>(Event.ANY, "DOWNLOAD");
    /**
     * Event at the start of an edit.
     */
    public static final EventType<StudyGuideEvent> START_EDIT = new EventType<>(Event.ANY, "START_EDIT");
    /**
     * Event at the end of an edit (confirm or cancel).
     */
    public static final EventType<StudyGuideEvent> FINISH_EDIT = new EventType<>(Event.ANY, "FINISH_EDIT");
    /**
     * Event to favorite.
     */
    public static final EventType<StudyGuideEvent> FAVORITE = new EventType<>(Event.ANY, "FAVORITE");
    /**
     * Event to upload.
     */
    public static final EventType<StudyGuideEvent> UPLOAD = new EventType<>(Event.ANY, "UPLOAD");
    /**
     * Event to delete.
     */
    public static final EventType<StudyGuideEvent> DELETE = new EventType<>(Event.ANY, "DELETE");

    private final DisplayableStudyGuide studyGuide;
    private final boolean savedChanges;

    /**
     * Initializes a new StudyGuideEvent with the given subEvent that isnt FINISH_EDIT.
     * @param studyGuide The study guide associated with the event
     * @param eventType The event type
     */
    public StudyGuideEvent(DisplayableStudyGuide studyGuide, EventType<StudyGuideEvent> eventType) {
        if (eventType == FINISH_EDIT) {
            throw new IllegalArgumentException("FINISH_EDIT is not supported with this constructor");
        }
        this.studyGuide = studyGuide;
        this.savedChanges = false;
        super(eventType);
    }

    /**
     * Initializes a new FINISH_EDIT StudyGuideEvent.
     * @param studyGuide The study guide effected
     * @param savedChanges If the changes were saved or not
     */
    public StudyGuideEvent(DisplayableStudyGuide studyGuide, boolean savedChanges) {
        this.studyGuide = studyGuide;
        this.savedChanges = savedChanges;
        super(StudyGuideEvent.FINISH_EDIT);
    }

    /**
     * Gets the study guide the event was called for.
     * @return The study guide
     */
    public DisplayableStudyGuide getStudyGuide() {
        return this.studyGuide;
    }

    /**
     * Gets a boolean for if the changes were saved.
     * @return A bool for if the changes were saved
     */
    public boolean getSavedChanges() {
        return this.savedChanges;
    }
}
