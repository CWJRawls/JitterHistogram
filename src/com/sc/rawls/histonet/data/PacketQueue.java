package com.sc.rawls.histonet.data;

import java.util.LinkedList;
import java.util.Queue;

import com.sc.rawls.histonet.net.JitMatrixPacket;

public class PacketQueue {
	
	private Queue<JitMatrixPacket> packQ;
	
	//default constructor
	public PacketQueue()
	{
		packQ = new LinkedList<JitMatrixPacket>();
	}
	
	//should always be called prior to getNext(), also serves as a check in getNext()
	public boolean hasNext()
	{
		return packQ.peek() != null ? true : false;
	}
	
	//returns the next element if there is one, otherwise null
	public JitMatrixPacket getNext()
	{
		return hasNext() == true ? packQ.poll() : null;
	}
	
	//adds a matrix packet to the list
	public void add(JitMatrixPacket jmp)
	{
		JitMatrixPacket temp = new JitMatrixPacket();
		temp.clone(jmp);
		packQ.add(temp);
	}
	
	//iterates and removes all elements
	public void clear()
	{
		while(packQ.poll() != null)
		{}
	}

}
