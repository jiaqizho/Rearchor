package net.smaliunxer.Reactor;

import java.util.concurrent.Semaphore;

public abstract class AbsReaction implements Reaction,Runnable{
	
	@Override
	public void run() {
		
		react();
	}
	
}