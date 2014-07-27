package simpledb;

/**
 * Transaction is helper class to represent a transaction ID with
 * a specified permission.
 */
public class Transaction{
  private TransactionId tid;
  private Permissions pms;
  
  public Transaction(TransactionId tid, Permissions pms){
    this.tid = tid;
    this.pms = pms;
  } 
  public TransactionId getId(){
    return tid;
  }
  public Permissions getPermission(){
    return pms;
  }
}
