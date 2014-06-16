package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

    /**
     * A help class to facilitate organizing the information of each field
     * */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The type of the field
         * */
        Type fieldType;
        
        /**
         * The name of the field
         * */
        String fieldName;

        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }
        //This constructor should nerver be used except runing test.
        public TDItem(){
          this(INT_TYPE, null);
        }

        /**
         * Verify whether two TDItem are equal.
         * If equal, return true, false otherwise. If and only if 
         * the two TDItem have exactly the same pattern, we set it true.
         * */
        public static boolean equals(TDItem td){
          return (this.fieldName.compareTo(td.fieldName) == 0 &&
                  this.fieldType.compareTo(td.fieldType) == 0) 
        }

        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }
    }

    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
        // some code goes here
        return new IDItem();
    }

    private static final long serialVersionUID = 1L;
    /**
     * The number of fields of this tuple.
     * */
    private int fieldNum; 

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *            array specifying the names of the fields. Note that names may
     *            be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        // some code goes here
        
    }

    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        // some code goes here
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        // some code goes here
        return this.fieldNum;
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     * 
     * @param i
     *            index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        // some code goes here
        if(i < fieldNum && i >= 0){
          Iterator<TDItem> iter = iterator();
          while(i-- > 0){
            // here may throw an exception.
            iter.next();
          } 
          return (iter.next()).fieldName;
        } else{
          throw new NoSuchElementException("invalid index")
        }
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     * 
     * @param i
     *            The index of the field to get the type of. It must be a valid
     *            index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        // some code goes here
        if(i < fieldNum && i >= 0){
          Iterator<TDItem> iter = iterator();
          while(i-- > 0){
            // here may throw an exception.
            iter.next();
          } 
          return (iter.next()).fieldType;
        } else{
          throw new NoSuchElementException("invalid index")
        }
    }

    /**
     * Find the index of the field with a given name.
     * 
     * @param name
     *            name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *             if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
        // some code goes here
        TDItem tmp = null;
        int i = 0;
        Iterator<TDItem> iter = iterator();
        while(iter.hasNext()){
          tmp = iter.next();
          if(tmp.fieldName.compareTo(name) == 0){
            return i; 
          } 
          ++i; 
        } 
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        // some code goes here
        TDItem tmp = null;
        int size = 0;
        Iterator<TDItem> iter = iterator();
        while(iter.hasNext()){
          tmp = iter.next();
          size += tmp.fieldType.getLen();
        }  
        return size;
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     * 
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        // some code goes here
        return null;
    }

    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they are the same size and if the n-th
     * type in this TupleDesc is equal to the n-th type in td.
     * 
     * @param o
     *            the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) {
        // some code goes here
        if(this.fieldNum == tmp.fieldNum){
          TDItem tdif, tdis;
          TupleDesc tud = (TupelDesc) o;
          Iterator<TDItem> iterf = iterator();
          Iterator<TDItem> iters = tud.iterator();
          while(iterf.hasNext() && iters.hasNext()){
            if(!iterf.next().equals(iters.next())){
              return false;
            }
          }
          return true;
        }
        return false;
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        throw new UnsupportedOperationException("unimplemented");
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * 
     * @return String describing this descriptor.
     */
    public String toString() {
        // some code goes here
        StringBuilder result = new StringBuilder();
        TDItem tdi = null;
        int index = 0;
        Iterator<TDItem> iter = iterator();
        while(iter.hasNext()){
          tdi = iter.next();
          result.append(String.format("%s[%d](%s[%d]),",
                        tdi.fieldType, index, tdi.fieldName, index));
          ++index; 
        } 
        result.delete(result.length()-1);
        return result;
    }
}
