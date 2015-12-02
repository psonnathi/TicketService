package org.service.enums;

public enum Status {

	HOLD('H'), RESERVE('R');
    private char status;

    private Status(char status) {
            this.status = status;
    }
    
    public char getStatus(Status status){
    	return status.status;
    }

}
