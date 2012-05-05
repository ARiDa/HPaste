package com.gravity.hbase.schema

import scala.collection.mutable.Buffer
import org.apache.hadoop.hbase.client.Delete

/*             )\._.,--....,'``.
 .b--.        /;   _.. \   _\  (`._ ,.
`=,-,-'~~~   `----(,_..'--(,_..'`-.;.'  */

/**
 * A deletion operation.  If nothing is specified but a key, will delete the whole row.
 * If a family is specified, will just delete the values in
 * that family.
 * @param table
 * @param key
 * @param previous
 * @tparam T
 * @tparam R
 */
class DeleteOp[T <: HbaseTable[T, R, _], R](table: HbaseTable[T, R, _], key: Array[Byte], previous: Buffer[OpBase[T, R]] = Buffer[OpBase[T, R]]()) extends OpBase[T, R](table, key, previous) {
  val delete = new Delete(key)

  def family[F, K, V](family: (T) => ColumnFamily[T, R, F, K, V]) = {
    val fam = family(table.pops)
    delete.deleteFamily(fam.familyBytes)
    this
  }

  def values[F, K, V](family: (T) => ColumnFamily[T, R, F, K, V], qualifiers: Set[K]) = {
    val fam = family(table.pops)
    for (q <- qualifiers) {
      delete.deleteColumns(fam.familyBytes, fam.keyConverter.toBytes(q))
    }
    this
  }
}
