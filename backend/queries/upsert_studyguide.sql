MERGE Studyguide AS target
    USING
        (VALUES (@studyguideId, @title, @description, @accountId))
        AS source (id, title, description, accountId)
    ON
        target.id = source.id
    WHEN MATCHED THEN
        UPDATE SET
            target.title = source.title,
            target.description = source.description
    WHEN NOT MATCHED THEN
        INSERT
            (id, title, description, idAccount)
        VALUES
            (source.id, source.title, source.description, source.accountId);

DELETE FROM
    Question
WHERE
    Question.idStudyguide = @studyguideId