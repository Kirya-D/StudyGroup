DELETE FROM Studyguide
    WHERE
        id = @id
        AND idAccount = @creatorId