package com.irosu.pathing

import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

//TODO: no se reinician los datos con cada test
private var HEIGHT: Int = 0
private var WIDTH: Int = 0

private val map = ArrayList<ArrayList<Node>>()
//TODO: revisar
private val cleanMap = ArrayList<ArrayList<Node>>()

private var START = Node(0, 0, Node.Type.START)
private val END = Node(0, 0, Node.Type.END)

private var node = START
private var lastNode: Node = node
private val currentNodes = arrayListOf(node)
private val nextNodes = ArrayList<Node>()

fun addPath(mapString: String): String {

    //Creamos el mapa a partir del String
    createMap(mapString)

    //Aplicamos el algoritmo para calcular la ruta más corta
    mainLoop()

    //Dibujamos la ruta en el mapa
    drawPath()

    //Devolvemos el mapa en forma de String
    return printMap()
}

private fun mainLoop(): Boolean {

    for (n in currentNodes) {
        if (checkNeighbours(n)) return true

        //TODO: comprobar si no puedo avanzar a ningún sitio (mapas irresolubles)
        if (nextNodes.isEmpty()) return true else node = nextNodes[0]
    }

    currentNodes.clear()
    currentNodes.addAll(nextNodes)

    mainLoop()
    return true
}

private fun checkNeighbours(node: Node): Boolean {

    val x = node.x
    val y = node.y

    //Marcamos y comprobamos los nodos a los que podemos movernos
    if (isNodeInside(x + 1, y + 1) && checkNode(map[x + 1][y + 1])) return true
    if (isNodeInside(x + 1, y) && checkNode(map[x + 1][y])) return true
    if (isNodeInside(x + 1, y - 1) && checkNode(map[x + 1][y - 1])) return true
    if (isNodeInside(x, y - 1) && checkNode(map[x][y - 1])) return true
    if (isNodeInside(x - 1, y - 1) && checkNode(map[x - 1][y - 1])) return true
    if (isNodeInside(x - 1, y) && checkNode(map[x - 1][y])) return true
    if (isNodeInside(x - 1, y + 1) && checkNode(map[x - 1][y + 1])) return true
    if (isNodeInside(x, y + 1) && checkNode(map[x][y + 1])) return true

    nextNodes.remove(node)

    return false
}

private fun checkNode(nextNode: Node): Boolean {

    if (nextNode.isPassable() && !currentNodes.contains(nextNode)) {
        nextNode.parent = findClosestParent(nextNode)
        if (nextNode.type != Node.Type.END) nextNode.type = Node.Type.CHECKED
        nextNodes.add(nextNode)
        lastNode = nextNode
    }

    return nextNode == END
}

/**
 * Busca el nodo vecino del pulso anterior (posibles padres) más cercano al inicio
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

    if (isNodeInside(x + 1, y + 1)) neighbours.add(map[x + 1][y + 1])
    if (isNodeInside(x + 1, y)) neighbours.add(map[x + 1][y])
    if (isNodeInside(x + 1, y - 1)) neighbours.add(map[x + 1][y - 1])
    if (isNodeInside(x, y - 1)) neighbours.add(map[x][y - 1])
    if (isNodeInside(x - 1, y - 1)) neighbours.add(map[x - 1][y - 1])
    if (isNodeInside(x - 1, y)) neighbours.add(map[x - 1][y])
    if (isNodeInside(x - 1, y + 1)) neighbours.add(map[x - 1][y + 1])
    if (isNodeInside(x, y + 1)) neighbours.add(map[x][y + 1])

    return neighbours
}

private fun isNodeInside(x: Int, y: Int) = x in 0 until HEIGHT && y in 0 until WIDTH

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

    //Quitar para que marque el inicio
    shortestPath.add(lastNode)

    shortestPath.forEach {
        //Añadir para que marque el final
        //if (cleanMap[it.x][it.y].type != Node.Type.END)
            cleanMap[it.x][it.y].type = Node.Type.PATH
    }

    for (arrayList in cleanMap) {
        arrayList.forEach {
            if (it.type == Node.Type.CHECKED)
                it.type = Node.Type.FREE
        }
    }
}

/**
 * Transforma el mapa en String a un array bidimensional de nodos
 */
fun createMap(mapString: String) {
    var x = 0
    var y = 0

    map.add(ArrayList<Node>())
    cleanMap.add(ArrayList<Node>())

    mapString.forEach {
        when(it) {
            '\n' -> {
                map.add(ArrayList<Node>())
                cleanMap.add(ArrayList<Node>())
                x++
                y=-1
            }
            'S' -> {
                map[x].add(Node(x,y, Node.Type.START))
                cleanMap[x].add(Node(x,y, Node.Type.START))
                START.x = x
                START.y = y
            }
            'X' -> {
                map[x].add(Node(x,y, Node.Type.END))
                cleanMap[x].add(Node(x,y, Node.Type.END))
                END.x = x
                END.y = y
            }
            'B' -> {
                map[x].add(Node(x,y, Node.Type.BLOCK))
                cleanMap[x].add(Node(x,y, Node.Type.BLOCK))
            }
            else -> {
                map[x].add(Node(x,y, Node.Type.FREE))
                cleanMap[x].add(Node(x,y, Node.Type.FREE))
            }
        }
        y++
    }

    HEIGHT = map.size
    WIDTH = map[0].size
}

private fun printMap(): String {
    var marked = ""
    cleanMap.forEach { c ->
        c.forEach { marked += "$it" }
        marked += "\n"
    }
    marked += "\n"

    return marked.trim()
}

//TODO: sólo para pruebas
private fun showNodes(nodes: ArrayList<Node>) {
    cleanMap.forEach { it.forEach { i -> if (i.type == Node.Type.PATH) i.type = Node.Type.FREE } }
    nodes.forEach { cleanMap[it.x][it.y].type = Node.Type.PATH }
    printMap()
    println("-----------------------------------------------------------------\n")
}
