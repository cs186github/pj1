package simpledb;

import java.io.*;

import java.util.LinkedList;
import java.util.HashTable;

/**
 * BufferPool manages the reading and writing of pages into memory from
 * disk. Access methods call into it to retrieve pages, and it fetches
 * pages from the appropriate location.
 * <p>
 * The BufferPool is also responsible for locking;  when a transaction fetches
 * a page, BufferPool checks that the transaction has the appropriate
 * locks to read/write the page.
 */
public class BufferPool {
    /** Bytes per page, including header. */
    public static final int PAGE_SIZE = 4096;

    /** Default number of pages passed to the constructor. This is used by
    other classes. BufferPool should use the numPages argument to the
    constructor instead. */
    public static final int DEFAULT_PAGES = 50;
 
    /**
     * Indicates a certain slot is available, that is the slot is not occupied
     * by a page.
     */
    public static final int AVAILABLE = 0;
    /**
     * Indicates a certain slot is refrenced at least once.
     */
    public static final int REFERENCED = 1;
    /**
     * Indicates a certain slot has ever been referenced and is not been 
     * referenced now. It is waiting for rereferenced.
     */
    public static final int WAITING = 2;
    /**
     * This is used in the clock replace algorithm.
     */
    private static final int CLOCKON = 3;

    /**
     * A frame in the buffer pool holds a page that have been retrived
     * from the disk.
     */
    public class Frame{
      private Page frame;
     /**
      * Keeps track of the times of the corresponding page being reference.
      */
      private int pinCount;
     /**
      * Keeps track of all of the trasations that hold this page.
      */ 
      private LinkedList<Transaction> traid; 
       
      /**
       * Construct a frame of the buffer pool.
       * @param frame the page that will occupy this frame.
       * @param tid the transaction identifier that want to hold this frame.
       * @param pms the permission of the current transaction.
       */
      public Frame(Page frame, TransactionId tid, Permissions pms){
	this.frame = frame;
	this.pinCount = 0; 
        Transaction tas = new Transaction(tid, pms);
        this.traid = new LinkedList<Transaction>(); 
	if(traid != null){
	  this.traid.add(tas);
	}
      }
      /**
       * Add a transaction identifier that want to hold this frame.
       * @param tid the transation identifier that want to hold this frame.
       * @param pms the permmsion of the current transaction. 'null' is ok.
       */
      public void addTransaction(TransactionId tid, Permissions pms){
	if(tid != null){
	  Transaction tas = new Transaction(tid, pms);
	  this.traid.add(tas);
	} else{
	  Debug.log("!!!warning: a 'null' transation ID is going to get a page. Adding refused."); 
	}
      }
    }
   
    /**
     * The frame pool of this buffer. There is only frame pool in a database.
     */
    private static HashTable<PageId, Frame> pool;
    /**
     * Keeps track of which slot is holding a specific page.
     */
    private static PageId[] slots;
    /**
     * The maximum number of frames in this buffer.
     */
    private static final int capacity;
    /**
     * Indicates the state of a slot. There are three states: 'AVAILABLE',
     * 'REFERENCED', 'WAITING', 'CLOCKON' in all. 
     */
    private static int state[];
   
    /**
     * Creates a BufferPool that caches up to numPages pages.
     *
     * @param numPages maximum number of pages in this buffer pool.
     */
    public BufferPool(int numPages) {
        // some code goes here

 	// Create a page pool to hold pages.
        pool = new HashTable<PageId, Frame>();
        capacity = numPages;
	slots = new PageId[capacity];
        // this should be initialized atomatically to zeros.
        state = new int[capacity]; 
    }

    /**
     * Retrieve the specified page with the associated permissions.
     * Will acquire a lock and may block if that lock is held by another
     * transaction.
     * <p>
     * The retrieved page should be looked up in the buffer pool.  If it
     * is present, it should be returned.  If it is not present, it should
     * be added to the buffer pool and returned.  If there is insufficient
     * space in the buffer pool, an page should be evicted and the new page
     * should be added in its place.
     *
     * @param tid the ID of the transaction requesting the page
     * @param pid the ID of the requested page
     * @param perm the requested permissions on the page
     */
    public  Page getPage(TransactionId tid, PageId pid, Permissions perm)
        throws TransactionAbortedException, DbException {
        // some code goes here
	Page pgNew;

	if(pool.containsKey(pid)){
	  Frame frm = pool.get(pid);
	  // do some lock check and permission check. Not necessary for pj1
	  frm.addTransaction(tid, perm);
	  frm.pinCount++;
          pgNew = frm.frame; 

  	} else {
	  Page pgSwap;
	  // If the buffer pool is full, maybe need replacing.
	  if(this.capacity == pool.size()){
	    pgSwap = clockReplacer();
	    // if there is not a proper page to be replaced, just forget it.
	    if(pgSwap != null){
	      try{
	        flushPage(pgSwap.getId());
              } catch (IOException ioe) {
	        ioe.printStaticTrace();
              } 
	      if(pool.remove(pgSwap.getId()) == null){
	        Debug.log("!!!warning: PageId \'" + pgSwap.getId().toString()
	     	  +"\' does not exist in the buffer poll, but need to be removed."); 
	      }
	    
	      // update state and slots info
	      int i = 0;
	      while(i < this.capacity){
	        if(state[i] == CLOCKON){
	       	  state[i] = REFERENCED;
		  slots[i] = pid;
		  break;
	        }
	        i++;
	      } 
	    } else {
	      return null;
	    }  
	  } else {
	    // update the state and slots info.
	    int i = 0;
	    while(i < this.capacity){
	      if(state[i] == AVAILABLE){
		state[i] = REFERENCED;
		slots[i] = pid;
		break;
	      }
	      i++;
	    }
	  }
	  // Read the page.
	  Catalog syscal = DataBase.getCatalog();
	  pgNew = syscal.getDbFile(pid.getTableId()).readPage(pid);
	  pool.put(pid, new Frame(pgNew, tid, perm));   
	}
        return pgNew; 
    }
    /**
     * Find a non-referenced frame in this buffer pool and return it. 
     * In this replacer, we use a clock strategy. Only and only if the
     * buffer pool is fully occupied the replacer would return a page for
     * replacing, fail to return a page for replacing otherwise.
     * @return the page for replacing. If no candidates, return null.
     */
    private Page clockReplacer(){
	if(this.capacity == pool.size){
	  int i = 0;
	  while(i < this.capacity){
	    if((slots[i] != null) && ((pool.get(slots[i])).pinCount == 0)){
	      state[i] = WAITING;
	    }
	    i++;
	  }
	  i = 0;
	  while(i < this.capacity){
	    if(state[i] == WAITING){
	      state[i] = CLOCKON;
	      return (pool.get(slots[i])).frame;  
	    }
	  }  
	} else {
	  // the program should never get here.
	  Debug.log("!!!warning: the replacer is malfunctioning. There still"
		+ " has unused frame but the buffer manager seems to hunt for "
		+ "a page for replacing.");
	}
	return null;
    }

    /**
     * Releases the lock on a page.
     * Calling this is very risky, and may result in wrong behavior. Think hard
     * about who needs to call this and why, and why they can run the risk of
     * calling it.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param pid the ID of the page to unlock
     */
    public  void releasePage(TransactionId tid, PageId pid) {
        // some code goes here
        // not necessary for proj1
    }

    /**
     * Release all locks associated with a given transaction.
     *
     * @param tid the ID of the transaction requesting the unlock
     */
    public void transactionComplete(TransactionId tid) throws IOException {
        // some code goes here
        // not necessary for proj1
    }

    /** Return true if the specified transaction has a lock on the specified page */
    public boolean holdsLock(TransactionId tid, PageId p) {
        // some code goes here
        // not necessary for proj1
        return false;
    }

    /**
     * Commit or abort a given transaction; release all locks associated to
     * the transaction.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param commit a flag indicating whether we should commit or abort
     */
    public void transactionComplete(TransactionId tid, boolean commit)
        throws IOException {
        // some code goes here
        // not necessary for proj1
    }

    /**
     * Add a tuple to the specified table behalf of transaction tid.  Will
     * acquire a write lock on the page the tuple is added to(Lock 
     * acquisition is not needed for lab2). May block if the lock cannot 
     * be acquired.
     * 
     * Marks any pages that were dirtied by the operation as dirty by calling
     * their markDirty bit, and updates cached versions of any pages that have 
     * been dirtied so that future requests see up-to-date pages. 
     *
     * @param tid the transaction adding the tuple
     * @param tableId the table to add the tuple to
     * @param t the tuple to add
     */
    public void insertTuple(TransactionId tid, int tableId, Tuple t)
        throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        // not necessary for proj1
    }

    /**
     * Remove the specified tuple from the buffer pool.
     * Will acquire a write lock on the page the tuple is removed from. May block if
     * the lock cannot be acquired.
     *
     * Marks any pages that were dirtied by the operation as dirty by calling
     * their markDirty bit.  Does not need to update cached versions of any pages that have 
     * been dirtied, as it is not possible that a new page was created during the deletion
     * (note difference from addTuple).
     *
     * @param tid the transaction adding the tuple.
     * @param t the tuple to add
     */
    public  void deleteTuple(TransactionId tid, Tuple t)
        throws DbException, TransactionAbortedException {
        // some code goes here
        // not necessary for proj1
    }

    /**
     * Flush all dirty pages to disk.
     * NB: Be careful using this routine -- it writes dirty data to disk so will
     *     break simpledb if running in NO STEAL mode.
     */
    public synchronized void flushAllPages() throws IOException {
        // some code goes here
        // not necessary for proj1

    }

    /** Remove the specific page id from the buffer pool.
        Needed by the recovery manager to ensure that the
        buffer pool doesn't keep a rolled back page in its
        cache.
    */
    public synchronized void discardPage(PageId pid) {
        // some code goes here
	// not necessary for proj1
    }

    /**
     * Flushes a certain page to disk
     * @param pid an ID indicating the page to flush
     */
    private synchronized  void flushPage(PageId pid) throws IOException {
        // some code goes here
        // not necessary for proj1
    }

    /** Write all pages of the specified transaction to disk.
     */
    public synchronized  void flushPages(TransactionId tid) throws IOException {
        // some code goes here
        // not necessary for proj1
    }

    /**
     * Discards a page from the buffer pool.
     * Flushes the page to disk to ensure dirty pages are updated on disk.
     */
    private synchronized  void evictPage() throws DbException {
        // some code goes here
        // not necessary for proj1
    }

}
