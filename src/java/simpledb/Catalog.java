package simpledb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * The Catalog keeps track of all available tables in the database and their
 * associated schemas.
 * For now, this is a stub catalog that must be populated with tables by a
 * user program before it can be used -- eventually, this should be converted
 * to a catalog that reads a catalog table from disk.
 */

public class Catalog {
   /**
    * A help interface to facilitate organizating the information of each
    * entry in a system catalog. All of the entries in catalog should 
    * implement this interface.
    */ 
   public abstract CatalogEntry{
     
     /**
      * Specify a unique entry in the system catalog. For each entry in a catalog
      * this item should be unique. 
      */
     private int entryID; 
     /**
      * Specify a name for this catalog entry.
      */
     private String entryName;

     /**
      * Return the string format of this entry.
      */ 
     public String toString();
   }
   /**
    * Instance of CatalogItem that stores the information about a heap file.
    *
    * Inherited fields:
    * 		private int entryID
    *		private String entryName 
    * 
    * Also this catalog entry maintains such specific information:
    * 		(1)The primary key of this table
    *		(2)The corresponding tuple desicription of this table, which 
    *		   contains the data schema.
    *		(3)The number of pages that this table has occupied.
    *		(4)The header of the page, which records whether a slot a occupied 
    *		   or not.
    *		(5)The number of slots of a heap page.
    *
    */ 
   public class CatalogHeap implements CatalogEntry{
     /**
      * The primary key of this relation.
      */
     private String pkey;
     /**
      * References the TupleDesc of this relation.
      */
     private TupleDesc tds;
     /**
      * The number of pages this table has.
      */
     private int numPage;
     /**
      * The length of header part in a HeapPage, Caculated by bytes.
      */
     private int headerLen;
     /**
      * The number of slots in a HeapPage.
      */
     private int numSlots;
     /**
      * The DbFile instance of this heap file.
      */
     private DbFile dbf;

     ///**
     // * The location of this table on the system disk.
     // */
     //private String tableLoc;

     /**
      * Constructs a catalog entry storing information about a heap table.
      * 
      * @param file the DbFile instance of this heap fle. 
      * @param tname the name of this relation.
      * @param pkey specifies the primary key of this relation.
      * @param tds specifies ths skema of this relation. 
      */
      public CatalogHeap(DbFile file, String tname, String pkey, TupleDesc tds){
        this.entryID = file.getId();
        this.entryName = tname;
        this.numPage = 0;
        this.pkey = pkey;
        this.tds = tds;
        this.dbf = file; 
        if(tds != null){
          this.numSlots = (BufferPool.PAGE_SIZE * 8)/(tds.getSize() * 8 + 1);
          this.headerLen = (int) Math.ceil(((double) this.numSlots)/8); 
        }
      } 

      /**
       * Return the String representation of this entry item.
       */
      public String toString(){
        return "This feature has not been implemented yet.\n"
      }
     
    
   }
  
    HashTable<Integer, CatalogEntry> catalog; 

    /**
     * Constructor.
     * Creates a new, empty catalog.
     */
    public Catalog() {
        // some code goes here
        catalog = new HashTable<Integer, CatalogEntry>();
    }

    /**
     * Add a new table to the catalog.
     * This table's contents are stored in the specified DbFile.
     * @param file the contents of the table to add;  file.getId() is the identfier of
     *    this file/tupledesc param for the calls getTupleDesc and getFile
     * @param name the name of the table -- may be an empty string.  May not be null.  If a name
     
     * conflict exists, use the last table to be added as the table for a given name.
     * @param pkeyField the name of the primary key field
     */
    public void addTable(DbFile file, String name, String pkeyField) {
        // some code goes here
        if(name == null){
          Debug.log("!!!warning: a table name of 'null' is founded.(Catalog.addTable)");
          name = String.valueOf(file.getId());
        }
        Catalog.CatalogHeap cah = Catalog.new CatalogHeap(file,
                                  name, pkeyField, file.getTupleDesc());  
        catalog.put(Integer.valueOf(file.getId())); 
    }

    public void addTable(DbFile file, String name) {
        addTable(file, name, "");
    }

    /**
     * Add a new table to the catalog.
     * This table has tuples formatted using the specified TupleDesc and its
     * contents are stored in the specified DbFile.
     * @param file the contents of the table to add;  file.getId() is the identfier of
     *    this file/tupledesc param for the calls getTupleDesc and getFile
     */
    public void addTable(DbFile file) {
        addTable(file, (UUID.randomUUID()).toString());
    }

    /**
     * Return the id of the table with a specified name,
     * @throws NoSuchElementException if the table doesn't exist
     */
    public int getTableId(String name) throws NoSuchElementException {
        // some code goes here
        return 1; 
 	 
    }

    /**
     * Returns the tuple descriptor (schema) of the specified table
     * @param tableid The id of the table, as specified by the DbFile.getId()
     *     function passed to addTable
     * @throws NoSuchElementException if the table doesn't exist
     */
    public TupleDesc getTupleDesc(int tableid) throws NoSuchElementException {
        // some code goes here
       Catalog.CatalogHeap cah= (Catalog.CatalogHeap) catalog.get(Integer.valueOf(tableid))
       if(cah == null){
         throw NoSuchElementException("no such table in the system catalog. The tableid is: " + String.valueOf(tableid));
       } else{
         return cah.tds;
       } 
    }

    /**
     * Returns the DbFile that can be used to read the contents of the
     * specified table.
     * @param tableid The id of the table, as specified by the DbFile.getId()
     *     function passed to addTable
     */
    public DbFile getDbFile(int tableid) throws NoSuchElementException {
        // some code goes here
        //return null;
        Catalog.CatalogHeap cah= (Catalog.CatalogHeap) catalog.get(Integer.valueOf(tableid))
       if(cah == null){
         throw NoSuchElementException("no such table in the system catalog. The tableid is: " + String.valueOf(tableid));
       } else{
         return cah.dbf;
       }
    }

    public String getPrimaryKey(int tableid) {
        // some code goes here
        // return null;
        Catalog.CatalogHeap cah= (Catalog.CatalogHeap) catalog.get(Integer.valueOf(tableid))
       if(cah == null){
         throw NoSuchElementException("no such table in the system catalog. The tableid is: " + String.valueOf(tableid));
       } else{
         return cah.pkey;
       }

    }

    public Iterator<Integer> tableIdIterator() {
        // some code goes here
        return null;
    }

    public String getTableName(int id) {
        // some code goes here
        // return null;
        Catalog.CatalogHeap cah= (Catalog.CatalogHeap) catalog.get(Integer.valueOf(tableid))
       if(cah == null){
         throw NoSuchElementException("no such table in the system catalog. The tableid is: " + String.valueOf(tableid));
       } else{
         return cah.entryName;
       }


    }
    
    /** Delete all tables from the catalog */
    public void clear() {
        // some code goes here
        System.out.println("----->Clearing the system catalog...");
        catalog.clear();
        System.out.println("----->System catalog clearing is done.");
    }
    
    /**
     * Reads the schema from a file and creates the appropriate tables in the database.
     * The format of a catalogFile looks like this:
     * 		TableName1(type field1 pk, type field2, type field3, ...)
     *          TableName2(type field1 pk, type field2, type field3, ...)
     *          TableName3(type field1 pk, type field2, type field3, ...)
     *          TableName4(type field1 pk, type field2, type field3, ...)
     *			.
     *			.
     *			.
     *          TableNameN(type field1 pk, type field2, type field3, ...)
     *
     * @param catalogFile
     */
    public void loadSchema(String catalogFile) {
        String line = "";
        String baseFolder=new File(catalogFile).getParent();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(catalogFile)));
            
            while ((line = br.readLine()) != null) {
                //assume line is of the format name (field type, field type, ...)
                String name = line.substring(0, line.indexOf("(")).trim();
                //System.out.println("TABLE NAME: " + name);
                String fields = line.substring(line.indexOf("(") + 1, line.indexOf(")")).trim();
                String[] els = fields.split(",");
                ArrayList<String> names = new ArrayList<String>();
                ArrayList<Type> types = new ArrayList<Type>();
                String primaryKey = "";
                for (String e : els) {
                    String[] els2 = e.trim().split(" ");
                    names.add(els2[0].trim());
                    if (els2[1].trim().toLowerCase().equals("int"))
                        types.add(Type.INT_TYPE);
                    else if (els2[1].trim().toLowerCase().equals("string"))
                        types.add(Type.STRING_TYPE);
                    else {
                        System.out.println("Unknown type " + els2[1]);
                        System.exit(0);
                    }
                    if (els2.length == 3) {
                        if (els2[2].trim().equals("pk"))
                            primaryKey = els2[0].trim();
                        else {
                            System.out.println("Unknown annotation " + els2[2]);
                            System.exit(0);
                        }
                    }
                }
                Type[] typeAr = types.toArray(new Type[0]);
                String[] namesAr = names.toArray(new String[0]);
                TupleDesc t = new TupleDesc(typeAr, namesAr);
                HeapFile tabHf = new HeapFile(new File(baseFolder+"/"+name + ".dat"), t);
                addTable(tabHf,name,primaryKey);
                System.out.println("Added table : " + name + " with schema " + t);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println ("Invalid catalog entry : " + line);
            System.exit(0);
        }
    }
}

