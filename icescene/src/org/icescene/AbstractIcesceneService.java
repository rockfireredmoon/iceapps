package org.icescene;

public class AbstractIcesceneService implements IcesceneService {

	private IcesceneApp app;

	@Override
	public void init(IcesceneApp app) {
		this.app =  app;		
	}
	

}
