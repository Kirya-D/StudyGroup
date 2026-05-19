package kirya.viewmodel;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javafx.beans.property.ListProperty;
import kirya.model.StudyGuide;
import kirya.utils.DisplayableStudyGuide;

public class TestRootDisplayViewmodel {

    @Nested
    public class TestConstructor {

        @Test
        public void testParameterlessDefaultMemberValues() {
            var viewmodel = new RootDisplayViewmodel();

            var expectedFavoritedType = ListProperty.class;
            var actualFavoritedType = viewmodel.getFavoritedStudyGuidesProperty();
            var expectedDownloadedType = ListProperty.class;
            var actualDownloadedType = viewmodel.getDownloadedStudyGuidesProperty();

            assertAll("Members",
                    () -> assertInstanceOf(expectedFavoritedType, actualFavoritedType),
                    () -> assertInstanceOf(expectedDownloadedType, actualDownloadedType));
        }
    }

    @Nested
    public class TestCreateNewStudyGuide {

        @Test
        public void testWhenSuccessful() {
            var viewmodel = new RootDisplayViewmodel();

            var expectedType = DisplayableStudyGuide.class;
            var actual = viewmodel.createNewStudyGuide();

            assertInstanceOf(expectedType, actual);
        }
    }

    @Nested
    public class TestDownloadStudyGuide {

        RootDisplayViewmodel viewmodel;
        StudyGuide studyGuide;

        @BeforeEach
        public void setup() {
            this.viewmodel = new RootDisplayViewmodel();
            this.studyGuide = new StudyGuide();
        }

        @Test
        public void testWhenNotAlreadyDownloaded() {
            this.viewmodel.downloadStudyGuide(this.studyGuide);

            var downloadedCollection = this.viewmodel.getDownloadedStudyGuidesProperty().get();
            var expectedCount = 1;
            var actualCount = downloadedCollection.size();

            assertAll("collection check",
                    () -> {
                        assertTrue(downloadedCollection.contains(this.studyGuide));
                    },
                    () -> {
                        assertEquals(expectedCount, actualCount);
                    });
        }

        @Test
        public void testWhenAlreadyDownloaded() {
            this.viewmodel.downloadStudyGuide(this.studyGuide);
            this.viewmodel.downloadStudyGuide(this.studyGuide);

            var downloadedCollection = this.viewmodel.getDownloadedStudyGuidesProperty().get();
            var expectedCount = 1;
            var actualCount = downloadedCollection.size();

            assertAll("collection check",
                    () -> {
                        assertTrue(downloadedCollection.contains(this.studyGuide));
                    },
                    () -> {
                        assertEquals(expectedCount, actualCount);
                    });
        }

        @Test
        public void testWhenOtherStudyGuidesAreAlreadyDownloaded() {
            this.viewmodel.downloadStudyGuide(new StudyGuide());
            this.viewmodel.downloadStudyGuide(new StudyGuide());
            this.viewmodel.downloadStudyGuide(this.studyGuide);

            var downloadedCollection = this.viewmodel.getDownloadedStudyGuidesProperty().get();
            var expectedCount = 3;
            var actualCount = downloadedCollection.size();

            assertAll("collection check",
                    () -> {
                        assertTrue(downloadedCollection.contains(this.studyGuide));
                    },
                    () -> {
                        assertEquals(expectedCount, actualCount);
                    });
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.viewmodel.downloadStudyGuide(null);
            });
        }
    }

    @Nested
    public class TestToggleFavoriteStudyGuide {

        RootDisplayViewmodel viewmodel;
        StudyGuide studyGuide;

        @BeforeEach
        public void setup() {
            this.viewmodel = new RootDisplayViewmodel();
            this.studyGuide = new StudyGuide();
        }

        @Test
        public void testWhenStudyGuideNotYetFavorited() {
            this.studyGuide.setIsFavorited(false);

            this.viewmodel.toggleFavoriteStudyGuide(this.studyGuide);

            var expectedFavorited = true;
            var actualFavorited = this.studyGuide.getIsFavorited();

            assertAll("Member checks",
                    () -> assertEquals(expectedFavorited, actualFavorited),
                    () -> assertTrue(this.viewmodel.getFavoritedStudyGuidesProperty().contains(this.studyGuide)));
        }

        @Test
        public void testWhenStudyGuideAlreadyFavorited() {
            this.studyGuide.setIsFavorited(true);
            this.viewmodel.getFavoritedStudyGuidesProperty().add(this.studyGuide);

            this.viewmodel.toggleFavoriteStudyGuide(this.studyGuide);

            var expectedFavorited = false;
            var actualFavorited = this.studyGuide.getIsFavorited();

            assertAll("Member checks",
                    () -> assertEquals(expectedFavorited, actualFavorited),
                    () -> assertTrue(!this.viewmodel.getFavoritedStudyGuidesProperty().contains(this.studyGuide)));
        }

        @Test
        public void throwsWhenStudyGuideIsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.viewmodel.toggleFavoriteStudyGuide(null);
            });
        }
    }

    @Nested
    public class TestDeleteStudyGuide {

        RootDisplayViewmodel viewmodel;
        StudyGuide studyGuide;

        @BeforeEach
        public void setup() {
            this.viewmodel = new RootDisplayViewmodel();
            this.studyGuide = new StudyGuide();
        }

        @Test
        public void testWhenAlreadyDeleted() {
            this.viewmodel.deleteStudyGuide(this.studyGuide);

            var downloadedCollection = this.viewmodel.getDownloadedStudyGuidesProperty().get();
            var expectedCount = 0;
            var actualCount = downloadedCollection.size();

            assertAll("collection check",
                    () -> {
                        assertTrue(!downloadedCollection.contains(this.studyGuide));
                    },
                    () -> {
                        assertEquals(expectedCount, actualCount);
                    });
        }

        @Test
        public void testWhenNotAlreadyDeleted() {
            this.viewmodel.downloadStudyGuide(this.studyGuide);
            this.viewmodel.deleteStudyGuide(this.studyGuide);

            var downloadedCollection = this.viewmodel.getDownloadedStudyGuidesProperty().get();
            var expectedCount = 0;
            var actualCount = downloadedCollection.size();

            assertAll("collection check",
                    () -> {
                        assertTrue(!downloadedCollection.contains(this.studyGuide));
                    },
                    () -> {
                        assertEquals(expectedCount, actualCount);
                    });
        }

        @Test
        public void testWhenOtherStudyGuidesAreCurrentlyDownloaded() {
            this.viewmodel.downloadStudyGuide(new StudyGuide());
            this.viewmodel.downloadStudyGuide(this.studyGuide);
            this.viewmodel.downloadStudyGuide(new StudyGuide());
            this.viewmodel.deleteStudyGuide(this.studyGuide);

            var downloadedCollection = this.viewmodel.getDownloadedStudyGuidesProperty().get();
            var expectedCount = 2;
            var actualCount = downloadedCollection.size();

            assertAll("collection check",
                    () -> {
                        assertTrue(!downloadedCollection.contains(this.studyGuide));
                    },
                    () -> {
                        assertEquals(expectedCount, actualCount);
                    });
        }

        @Test
        public void throwsWhenNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.viewmodel.deleteStudyGuide(null);
            });
        }
    }
}
