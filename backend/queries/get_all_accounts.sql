SELECT
    id,
    username,
    password
FROM
    Account;

SELECT
    AccSgStatus.idAccount,
    AccSgStatus.idStudyguide,
    AccSgStatus.favorited,
    AccSgStatus.downloaded
FROM
    AccountStudyguideStatus AS AccSgStatus
LEFT JOIN
    Account
ON
    AccSgStatus.idAccount = Account.id;
