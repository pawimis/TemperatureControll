<?php
if($_SERVER['REQUEST_METHOD'] == 'POST'){
 	require_once('vars.php');
	 $dbc = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME)
		or die('No SQL server connection');
	$token = $_POST['token'];
	if(!empty($token)){
		$query = "SELECT * FROM UserDatabase WHERE Token = '$token'";
		$check = mysqli_fetch_array(mysqli_query($dbc,$query));
		if(isset($check)){
			echo "OK";
                }
		else{
			echo "ERROR" ;
		}
	}
	else{
		echo "ERROR2";
	}
}else{
	echo "non POST err";
}
?>
