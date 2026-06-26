MERGE AccountStudyguideStatus AS target
USING
    (VALUES (@accountId, @studyguideId, @favorited, @downloaded))
    AS source (accountId, studyguideId, favorited, downloaded)
ON
    target.idAccount = source.accountId
    AND target.idStudyguide = source.studyguideId
WHEN MATCHED THEN
    UPDATE SET
        target.favorited = source.favorited,
        target.downloaded = source.downloaded
WHEN NOT MATCHED THEN
    INSERT
        (idAccount, idStudyguide, favorited, downloaded)
    VALUES
        (source.accountId, source.studyguideId, source.favorited, source.downloaded);