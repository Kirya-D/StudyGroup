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
            var expectedUploadedType = ListProperty.class;
            var actualUploadedType = viewmodel.getUploadedStudyGuidesProperty();

            assertAll("Members",
                    () -> assertInstanceOf(expectedDownloadedType, actualDownloadedType),
                    () -> assertInstanceOf(expectedFavoritedType, actualFavoritedType),
                    () -> assertInstanceOf(expectedUploadedType, actualUploadedType));
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
    public class TestToggleDownloadStudyGuide {

        RootDisplayViewmodel viewmodel;
        StudyGuide studyGuide;

        @BeforeEach
        public void setup() {
            this.viewmodel = new RootDisplayViewmodel();
            this.studyGuide = new StudyGuide();
        }

        @Test
        public void testWhenToggleIsTrue() {
            this.viewmodel.toggleDownloadStudyGuide(this.studyGuide, true);

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
        public void testWhenToggleIsFalse() {
            this.viewmodel.toggleDownloadStudyGuide(this.studyGuide, false);

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
        public void testToggleTrueWhenOtherStudyGuidesAreAlreadyDownloaded() {
            this.viewmodel.toggleDownloadStudyGuide(new StudyGuide(), true);
            this.viewmodel.toggleDownloadStudyGuide(new StudyGuide(), true);
            this.viewmodel.toggleDownloadStudyGuide(this.studyGuide, true);

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
        public void testToggleFalseWhenOtherStudyGuidesAreAlreadyDownloaded() {
            this.viewmodel.toggleDownloadStudyGuide(new StudyGuide(), true);
            this.viewmodel.toggleDownloadStudyGuide(new StudyGuide(), true);
            this.viewmodel.toggleDownloadStudyGuide(this.studyGuide, true);
            this.viewmodel.toggleDownloadStudyGuide(this.studyGuide, false);

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
                this.viewmodel.toggleDownloadStudyGuide(null, true);
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
        public void testWhenToggleIsTrue() {
            this.viewmodel.toggleFavoriteStudyGuide(this.studyGuide, true);

            var expectedFavorited = true;
            var actualFavorited = this.studyGuide.getIsFavorited();

            assertAll("Member checks",
                    () -> assertEquals(expectedFavorited, actualFavorited),
                    () -> assertTrue(this.viewmodel.getFavoritedStudyGuidesProperty().contains(this.studyGuide)));
        }

        @Test
        public void testWhenToggleIsFalse() {
            this.viewmodel.toggleFavoriteStudyGuide(this.studyGuide, false);

            var expectedFavorited = false;
            var actualFavorited = this.studyGuide.getIsFavorited();

            assertAll("Member checks",
                    () -> assertEquals(expectedFavorited, actualFavorited),
                    () -> assertTrue(!this.viewmodel.getFavoritedStudyGuidesProperty().contains(this.studyGuide)));
        }

        @Test
        public void testToggleTrueWhenOtherStudyGuidesAreAlreadyFavorited() {
            this.viewmodel.toggleFavoriteStudyGuide(new StudyGuide(), true);
            this.viewmodel.toggleFavoriteStudyGuide(new StudyGuide(), true);
            this.viewmodel.toggleFavoriteStudyGuide(this.studyGuide, true);

            var favoritedCollection = this.viewmodel.getFavoritedStudyGuidesProperty().get();
            var expectedCount = 3;
            var actualCount = favoritedCollection.size();

            assertAll("collection check",
                    () -> {
                        assertTrue(favoritedCollection.contains(this.studyGuide));
                    },
                    () -> {
                        assertEquals(expectedCount, actualCount);
                    });
        }

        @Test
        public void testToggleFalseWhenOtherStudyGuidesAreAlreadyFavorited() {
            this.viewmodel.toggleFavoriteStudyGuide(new StudyGuide(), true);
            this.viewmodel.toggleFavoriteStudyGuide(new StudyGuide(), true);
            this.viewmodel.toggleFavoriteStudyGuide(this.studyGuide, true);
            this.viewmodel.toggleFavoriteStudyGuide(this.studyGuide, false);

            var favoritedCollection = this.viewmodel.getFavoritedStudyGuidesProperty().get();
            var expectedCount = 2;
            var actualCount = favoritedCollection.size();

            assertAll("collection check",
                    () -> {
                        assertTrue(!favoritedCollection.contains(this.studyGuide));
                    },
                    () -> {
                        assertEquals(expectedCount, actualCount);
                    });
        }

        @Test
        public void throwsWhenStudyGuideIsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.viewmodel.toggleFavoriteStudyGuide(null, true);
            });
        }
    }

    @Nested
    public class TestToggleUploadStudyGuide {

        RootDisplayViewmodel viewmodel;
        StudyGuide studyGuide;

        @BeforeEach
        public void setup() {
            this.viewmodel = new RootDisplayViewmodel();
            this.studyGuide = new StudyGuide();
        }

        @Test
        public void testWhenToggleIsTrue() {
            this.viewmodel.toggleUploadStudyGuide(this.studyGuide, true);

            var expectedUploaded = true;
            var actualUploaded = this.studyGuide.getIsUploaded();

            assertAll("Member checks",
                    () -> assertEquals(expectedUploaded, actualUploaded),
                    () -> assertTrue(this.viewmodel.getUploadedStudyGuidesProperty().contains(this.studyGuide)));
        }

        @Test
        public void testWhenToggleIsFalse() {
            this.viewmodel.toggleUploadStudyGuide(this.studyGuide, false);

            var expectedUploaded = false;
            var actualUploaded = this.studyGuide.getIsUploaded();

            assertAll("Member checks",
                    () -> assertEquals(expectedUploaded, actualUploaded),
                    () -> assertTrue(!this.viewmodel.getUploadedStudyGuidesProperty().contains(this.studyGuide)));
        }

        @Test
        public void testToggleTrueWhenOtherStudyGuidesAreAlreadyFavorited() {
            this.viewmodel.toggleUploadStudyGuide(new StudyGuide(), true);
            this.viewmodel.toggleUploadStudyGuide(new StudyGuide(), true);
            this.viewmodel.toggleUploadStudyGuide(this.studyGuide, true);

            var uploadedCollection = this.viewmodel.getUploadedStudyGuidesProperty().get();
            var expectedCount = 3;
            var actualCount = uploadedCollection.size();

            assertAll("collection check",
                    () -> {
                        assertTrue(uploadedCollection.contains(this.studyGuide));
                    },
                    () -> {
                        assertEquals(expectedCount, actualCount);
                    });
        }

        @Test
        public void testToggleFalseWhenOtherStudyGuidesAreAlreadyFavorited() {
            this.viewmodel.toggleUploadStudyGuide(new StudyGuide(), true);
            this.viewmodel.toggleUploadStudyGuide(new StudyGuide(), true);
            this.viewmodel.toggleUploadStudyGuide(this.studyGuide, true);
            this.viewmodel.toggleUploadStudyGuide(this.studyGuide, false);

            var uploadedCollection = this.viewmodel.getUploadedStudyGuidesProperty().get();
            var expectedCount = 2;
            var actualCount = uploadedCollection.size();

            assertAll("collection check",
                    () -> {
                        assertTrue(!uploadedCollection.contains(this.studyGuide));
                    },
                    () -> {
                        assertEquals(expectedCount, actualCount);
                    });
        }

        @Test
        public void throwsWhenStudyGuideIsNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                this.viewmodel.toggleUploadStudyGuide(null, true);
            });
        }
    }
}
