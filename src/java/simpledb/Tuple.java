package simpledb;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Tuple maintains information about the contents of a tuple. Tuples have a
 * specified schema specified by a TupleDesc object and contain Field objects
 * with the data for each field.
 *
 * We use an array to hold the fields. It is allocated by calling constructor.
 */
@SuppressWarnings("unused")
public class Tuple implements Serializable {

    private static final long serialVersionUID = 1L;
   
    private Field[] fields; 
    private TupleDesc tud;
    private RecordId rid;

    /**
     * Create a new tuple with the specified schema (type).
     * 
     * @param td
     *            the schema of this tuple. It must be a valid TupleDesc
     *            instance with at least one field.
     */
    public Tuple(TupleDesc td) {
        // some code goes here
        if(td == null){
          // When get a null td, print warning.
          Debug.log("!!!Warning: a null TupleDesc is passed to Tuple constructor."); 
        }
        this.fields = new Field[td.numFields()];
        this.tud = td; 
        this.rid = null;
   }

    /**
     * @return The TupleDesc representing the schema of this tuple.
     *
     * Note: The returned TupleDesc is the same object that this Tuple used.
     *       Change the returned value, change the inner structure. Be careful.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        return this.tud;
    }

    /**
     * @return The RecordId representing the location of this tuple on disk. May
     *         be null.
     */
    public RecordId getRecordId() {
        // some code goes here
        return this.rid;
    }

    /**
     * Set the RecordId information for this tuple.
     * 
     * @param rid
     *            the new RecordId for this tuple.
     */
    public void setRecordId(RecordId rid) {
        // some code goes here
        this.rid = rid;
    }

    /**
     * Change the value of the ith field of this tuple.
     * 
     * @param i
     *            index of the field to change. It must be a valid index.
     * @param f
     *            new value for the field.
     */
    public void setField(int i, Field f) {
        // some code goes here
        if(i >= 0 && i < fields.length){
          this.fields[i] = f; 
        } else{
          Debug.log("!!!Fatal error: the index is invalid.(Tuple.setField).");
        } 
    }

    /**
     * @return the value of the ith field, or null if it has not been set.
     * 
     * @param i
     *            field index to return. Must be a valid index.
     */
    public Field getField(int i) {
        // some code goes here
        return this.fields[i];
    }

    /**
     * Returns the contents of this Tuple as a string. Note that to pass the
     * system tests, the format needs to be as follows:
     * 
     * column1\tcolumn2\tcolumn3\t...\tcolumnN\n
     * 
     * where \t is any whitespace, except newline, and \n is a newline
     */
    public String toString() {
        // some code goes here
        // throw new UnsupportedOperationException("Implement this");
 	StringBuilder result = new StringBuilder();
	for(Field f: fields){
	  result.append(f.toString());
	  result.append('\t');
	}
	result.delete(fields.length-1, fields.length);		
	result.append('\n');
	return result.toString();
    }
    
    /**
     * @return
     *        An iterator which iterates over all the fields of this tuple
     * */
    
    public Iterator<Field> fields()
    {
        // some code goes here
        class FieldIterator implements Iterator<Field>{
          int loc = 0; 
          public boolean hasNext(){
            return loc < fields.length;
          }
          public Field next(){
            loc++;
            return fields[loc-1]; 
          }
          public void remove(){
            throw new UnsupportedOperationException();
          }
        }
        return (Iterator<Field>)new FieldIterator();
    }

}
