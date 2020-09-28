package edu.uci.ics.gamendo1.service.api_gateway.threadpool;

import edu.uci.ics.gamendo1.service.api_gateway.logger.ServiceLogger;

public class ClientRequestQueue {
    private ListNode head;
    private ListNode tail;

    public ClientRequestQueue() {
        head = tail = null;
    }

    public synchronized void enqueue(ClientRequest clientRequest) {
        if (head == null)
        {
            head = new ListNode(clientRequest, null);
            tail = head;
        }
        else
        {
            head = new ListNode(clientRequest, head);
        }
        notify();
    }

    public synchronized ClientRequest dequeue() {
        try {
            while(head == null) {
                wait();
            }
            ListNode temp = head;
            head = head.getNext();
            if (head == null)
                tail = null;
            return temp.getClientRequest();
        }catch(InterruptedException e)
        {
            ServiceLogger.LOGGER.warning("Error in dequeue: " + e.getMessage());
            return null;
        }

    }

    boolean isEmpty() {
        return head == null;
    }

}
