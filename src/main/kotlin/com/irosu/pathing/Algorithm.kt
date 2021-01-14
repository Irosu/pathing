package com.irosu.pathing

import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

class Algorithm {

    private val map = ArrayList<ArrayList<Node>>()

    //TODO: revisar
    private val cleanMap = ArrayList<ArrayList<Node>>()
    private val START = Node(1, 7, Node.Type.START)
    private val END = Node(13, 8, Node.Type.END)

    private var node = START
    private val currentNodes = arrayListOf(node)
    private val nextNodes = ArrayList<Node>()
    private var lastNode: Node = node

    fun run() {
        setEmptyMap()
        setDefault()
        printMap(map)

        mainLoop()

        drawPath()
        printMap(cleanMap)
    }

    private fun mainLoop(): Boolean {

        for (n in currentNodes) {
            if (checkNeighbours(n)) return true

            //TODO: comprobar si no puedo avanzar a ningún sitio (mapas irresolubles)
            if (nextNodes.isEmpty()) return true else node = nextNodes[0]

            //printMap()
        }

        currentNodes.clear()
        currentNodes.addAll(nextNodes)
        //TODO
        //nextNodes.clear()
        //showNodes(currentNodes)

        mainLoop()
        return true
    }

    private fun checkNeighbours(node: Node): Boolean {
        
        val x = node.x
        val y = node.y
        val size = map.size

        //Marcamos y comprobamos los nodos a los que podemos movernos
        if (isNodeInside(x + 1, y + 1, size) && checkNode(map[x + 1][y + 1])) return true
        if (isNodeInside(x + 1, y, size) && checkNode(map[x + 1][y])) return true
        if (isNodeInside(x + 1, y - 1, size) && checkNode(map[x + 1][y - 1])) return true
        if (isNodeInside(x, y - 1, size) && checkNode(map[x][y - 1])) return true
        if (isNodeInside(x - 1, y - 1, size) && checkNode(map[x - 1][y - 1])) return true
        if (isNodeInside(x - 1, y, size) && checkNode(map[x - 1][y])) return true
        if (isNodeInside(x - 1, y + 1, size) && checkNode(map[x - 1][y + 1])) return true
        if (isNodeInside(x, y + 1, size) && checkNode(map[x][y + 1])) return true

        nextNodes.remove(node)

        return false
    }

    private fun checkNode(nextNode: Node): Boolean {

        if (nextNode.isPassable() && !currentNodes.contains(nextNode)) {
            //TODO: el nodo nuevo tiene que mirar en currentNodes,
            // coger los que sean vecinos suyos y escoger el más cercano al inicio
            nextNode.parent = findClosestParent(nextNode)
            if (nextNode.type != Node.Type.END) nextNode.type = Node.Type.CHECKED
            nextNodes.add(nextNode)
            lastNode = nextNode
        }

        return nextNode == END
    }

    /**
     * Busca el nodo vecino del pulso anterior (posibles padres) más cercano al inicio
     * TODO: revisar la lista de currentNodes !!!!
     */
    private fun findClosestParent(node: Node): Node {
        //1. lista con los vecinos
        val neighbours = getNeighbours(node)

        //2. sacamos los que estén en ambas
        neighbours.retainAll(currentNodes)

        //3. cogemos el más cercano al inicio
        return closestToStart(neighbours)
    }

    /**
     * Devuelve la lista de los nodos vecinos
     */
    private fun getNeighbours(node: Node): ArrayList<Node> {
        val neighbours = ArrayList<Node>()
        val x = node.x
        val y = node.y
        val size = map.size

        if (isNodeInside(x + 1, y + 1, size)) neighbours.add(map[x + 1][y + 1])
        if (isNodeInside(x + 1, y, size)) neighbours.add(map[x + 1][y])
        if (isNodeInside(x + 1, y - 1, size)) neighbours.add(map[x + 1][y - 1])
        if (isNodeInside(x, y - 1, size)) neighbours.add(map[x][y - 1])
        if (isNodeInside(x - 1, y - 1, size)) neighbours.add(map[x - 1][y - 1])
        if (isNodeInside(x - 1, y, size)) neighbours.add(map[x - 1][y])
        if (isNodeInside(x - 1, y + 1, size)) neighbours.add(map[x - 1][y + 1])
        if (isNodeInside(x, y + 1, size)) neighbours.add(map[x][y + 1])

        return neighbours
    }

    private fun isNodeInside(x: Int, y: Int, size: Int): Boolean {
        return x in 0 until size && y in 0 until size
    }

    /**
     * Devuelve el nodo más cerano a la salida
     * TODO: revisar
     */
    private fun closestToStart(nodes: ArrayList<Node>) = nodes.minByOrNull { distanceToStart(it) }!!

    private fun distanceToStart(node: Node): Double {
        return sqrt((START.x - node.x).toDouble().pow(2) + (START.y - node.y).toDouble().pow(2))
    }

    private fun drawPath() {
        val shortestPath = ArrayList<Node>()

        while (lastNode.type != Node.Type.START) {
            shortestPath.add(lastNode)
            lastNode = lastNode.parent!!
        }

        shortestPath.forEach {
            if (cleanMap[it.x][it.y].type != Node.Type.END) {
                cleanMap[it.x][it.y].type = Node.Type.PATH
            }
        }

        for (arrayList in cleanMap) {
            arrayList.forEach {
                if (it.type == Node.Type.CHECKED)
                    it.type = Node.Type.FREE
            }
        }
    }

    private fun printMap(map: ArrayList<ArrayList<Node>>) {
        map.forEach { c ->
            c.forEach { print("$it  ") }
            println()
        }
        println()
    }

    private fun setEmptyMap(side: Int = 20) {
        repeat((1..side).count()) { x ->
            val column = ArrayList<Node>()
            repeat((1..side).count()) { y -> column.add(Node(x, y)) }
            map.add(column)
        }
        repeat((1..side).count()) { x ->
            val column = ArrayList<Node>()
            repeat((1..side).count()) { y -> column.add(Node(x, y)) }
            cleanMap.add(column)
        }
    }

    private fun setDefault() {

        map[START.x][START.y].type = Node.Type.START
        map[END.x][END.y].type = Node.Type.END
        map[12][7].type = Node.Type.BLOCK
        map[13][7].type = Node.Type.BLOCK
        map[14][7].type = Node.Type.BLOCK
        map[15][7].type = Node.Type.BLOCK
        map[16][3].type = Node.Type.BLOCK
        map[16][4].type = Node.Type.BLOCK
        map[16][5].type = Node.Type.BLOCK
        map[16][6].type = Node.Type.BLOCK
        map[16][7].type = Node.Type.BLOCK
        map[16][8].type = Node.Type.BLOCK
        map[16][9].type = Node.Type.BLOCK
        map[16][10].type = Node.Type.BLOCK
        map[16][11].type = Node.Type.BLOCK
        map[12][8].type = Node.Type.BLOCK
        map[12][9].type = Node.Type.BLOCK
        map[12][10].type = Node.Type.BLOCK
        map[12][11].type = Node.Type.BLOCK
        map[12][12].type = Node.Type.BLOCK
        map[12][13].type = Node.Type.BLOCK
        map[12][14].type = Node.Type.BLOCK
        map[12][15].type = Node.Type.BLOCK
        map[12][16].type = Node.Type.BLOCK
        map[12][17].type = Node.Type.BLOCK
        map[12][18].type = Node.Type.BLOCK
        map[13][18].type = Node.Type.BLOCK
        map[14][18].type = Node.Type.BLOCK
        map[15][18].type = Node.Type.BLOCK
        map[16][18].type = Node.Type.BLOCK
        map[17][18].type = Node.Type.BLOCK
        map[18][18].type = Node.Type.BLOCK
        map[19][18].type = Node.Type.BLOCK


        cleanMap[START.x][START.y].type = Node.Type.START
        cleanMap[END.x][END.y].type = Node.Type.END
        cleanMap[12][7].type = Node.Type.BLOCK
        cleanMap[13][7].type = Node.Type.BLOCK
        cleanMap[14][7].type = Node.Type.BLOCK
        cleanMap[15][7].type = Node.Type.BLOCK
        cleanMap[16][3].type = Node.Type.BLOCK
        cleanMap[16][4].type = Node.Type.BLOCK
        cleanMap[16][5].type = Node.Type.BLOCK
        cleanMap[16][6].type = Node.Type.BLOCK
        cleanMap[16][7].type = Node.Type.BLOCK
        cleanMap[16][8].type = Node.Type.BLOCK
        cleanMap[16][9].type = Node.Type.BLOCK
        cleanMap[16][10].type = Node.Type.BLOCK
        cleanMap[16][11].type = Node.Type.BLOCK
        cleanMap[12][8].type = Node.Type.BLOCK
        cleanMap[12][9].type = Node.Type.BLOCK
        cleanMap[12][10].type = Node.Type.BLOCK
        cleanMap[12][11].type = Node.Type.BLOCK
        cleanMap[12][12].type = Node.Type.BLOCK
        cleanMap[12][13].type = Node.Type.BLOCK
        cleanMap[12][14].type = Node.Type.BLOCK
        cleanMap[12][15].type = Node.Type.BLOCK
        cleanMap[12][16].type = Node.Type.BLOCK
        cleanMap[12][17].type = Node.Type.BLOCK
        cleanMap[12][18].type = Node.Type.BLOCK
        cleanMap[13][18].type = Node.Type.BLOCK
        cleanMap[14][18].type = Node.Type.BLOCK
        cleanMap[15][18].type = Node.Type.BLOCK
        cleanMap[16][18].type = Node.Type.BLOCK
        cleanMap[17][18].type = Node.Type.BLOCK
        cleanMap[18][18].type = Node.Type.BLOCK
        cleanMap[19][18].type = Node.Type.BLOCK

    }

    //TODO: sólo para pruebas
    private fun showNodes(nodes: ArrayList<Node>) {
        cleanMap.forEach { it.forEach { i -> if (i.type == Node.Type.PATH) i.type = Node.Type.FREE } }
        nodes.forEach { cleanMap[it.x][it.y].type = Node.Type.PATH }
        printMap(cleanMap)
        println("-----------------------------------------------------------------\n")
    }
}