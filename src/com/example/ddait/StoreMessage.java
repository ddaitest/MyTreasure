package com.example.ddait;

import com.ddai.lib.reflectiondb.DBString;
import com.ddai.lib.reflectiondb.Entity;
import com.ddai.lib.reflectiondb.Transient;

@Entity(name = DBString.TABLE_TEST)
public class StoreMessage {

	// ID at Client DB.
	@Transient
	public long local_id = 0;
	
	public String request = "";
	
	public String response = "";
	
	public int timestamp = 0;
}
