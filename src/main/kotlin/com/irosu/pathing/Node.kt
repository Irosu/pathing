package com.irosu.pathing

data class Node(var x: Int, var y: Int, var type: Type = Type.FREE, var parent: Node? = null) {

    enum class Type(val graph: Char) {
        FREE('.'),
        BLOCK('B'),
        PATH('*'),
        START('S'),
        END('X'),
        CHECKED('#')
    }

    /**
     * Checks whether the the node is passable or not
     */
    fun isPassable() = type != Type.START && type != Type.BLOCK && type != Type.CHECKED && type != Type.PATH

    override fun toString() = "${type.graph}"
    override fun equals(other: Any?) = other != null && other is Node && other.x == x && other.y == y
    override fun hashCode() = 31 * x + y
}