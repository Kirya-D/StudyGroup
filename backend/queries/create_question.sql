DECLARE @insertedId TABLE (
    id INT
)
INSERT INTO
    Question (text, idStudyguide)
OUTPUT
    inserted.id
    INTO
        @insertedId (id)
VALUES
    (@text, @studyguideId)

SET @newId = (SELECT TOP 1 id FROM @insertedId)