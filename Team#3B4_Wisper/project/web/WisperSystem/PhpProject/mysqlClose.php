<?php

//Mysql切断($stmtまで切断, 二重でsql文が実行できないため)
function stmtClose($stmt) {
    //メモリの解放
    mysqli_stmt_free_result($stmt);
    //ステートメントを閉じる
    mysqli_stmt_close($stmt);
    //print '切断できました';
}

//Mysql切断(データベースまで切断)
function mysqlClose($stmt, $conn) {
    //メモリの解放
    mysqli_stmt_free_result($stmt);
    //ステートメントを閉じる
    mysqli_stmt_close($stmt);
    //データベースの接続を閉じる
    mysqli_close($conn);
    //print '切断できました';
}
?>

