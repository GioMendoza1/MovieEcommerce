DELIMITER #
CREATE PROCEDURE insert_sales_transactions
(IN pEmail VARCHAR(50), IN pMovieId VARCHAR(10), IN pQuantity INT, IN pSaleDate DATE, IN pToken VARCHAR(50), IN pTransactionId VARCHAR(50))
BEGIN
INSERT INTO sales (email, movieId, quantity, saleDate)
  VALUES(pEmail, pMovieId, pQuantity, pSaleDate);
INSERT INTO transactions (sId, token, transactionId)
  VALUES(LAST_INSERT_ID(), pToken, pTransactionId);
END #
DELIMITER ;