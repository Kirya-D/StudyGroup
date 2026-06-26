SELECT
    Studyguide.id,
    Studyguide.title,
    Studyguide.description,
    creatorAccount.id AS creatorId,
    (
        SELECT COUNT(*)
        FROM
            Question
        WHERE
            Question.idStudyguide = Studyguide.id
    ) AS questionCount
FROM
    Studyguide
LEFT JOIN Account creatorAccount
    ON Studyguide.idAccount = creatorAccount.id
WHERE
    creatorAccount.username LIKE @search
    OR Studyguide.title LIKE @search
    OR Studyguide.description LIKE @search
ORDER BY
    Studyguide.id
OFFSET (@offset * @maxResults) ROWS FETCH NEXT @maxResults ROWS ONLY 