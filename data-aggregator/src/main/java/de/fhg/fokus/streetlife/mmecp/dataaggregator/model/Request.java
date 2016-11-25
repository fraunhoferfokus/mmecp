package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import java.io.Serializable;

public class Request implements Serializable{

	protected String info;
	protected Context context;

	public String getInfo(){
		return this.info;
	}

	public void setInfo(String info){
		this.info = info;
	}
	
	public Context getContext(){
		if(this.context == null){
			this.context = new Context();
		}
		return this.context;
	}

	public void setContext(Context context){
		this.context = context;
	}
}