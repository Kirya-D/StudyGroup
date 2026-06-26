DECLARE @upsertGuideId TABLE (
    id INT
)
MERGE Studyguide AS target
    USING
        (VALUES (@studyguideId, @title, @description, @accountId))
        AS source (id, title, description, accountId)
    ON
        target.id = source.id
        AND target.idAccount = source.accountId
    WHEN MATCHED THEN
        UPDATE SET
            target.title = source.title,
            target.description = source.description
        
    WHEN NOT MATCHED THEN
        INSERT
            (title, description, idAccount)
        VALUES
            (source.title, source.description, source.accountId)

OUTPUT
    CASE $action
        WHEN 'UPDATE' THEN source.id
        WHEN 'INSERT' THEN inserted.id
    END AS id
INTO
    @upsertGuideId (id);

SET @newStudyguideId = (SELECT TOP 1 id FROM @upsertGuideId)

DELETE FROM
    Question
WHERE
    Question.idStudyguide = @newStudyguideId