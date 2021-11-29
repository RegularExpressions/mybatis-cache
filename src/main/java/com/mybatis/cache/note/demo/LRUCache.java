package com.mybatis.cache.note.demo;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    public int capacity; //容量
    public int size;//当前元素数量
    public DNodeList head;
    public DNodeList tail;
    public Map<Integer,DNodeList> cache = new HashMap<>();



    class DNodeList{
        int key;
        int value;
        DNodeList prev;
        DNodeList next;

        DNodeList(){

        }
        DNodeList(int key,int value){
            this.key = key;
            this.value = value;
            this.prev = null;
            this.next = null;
        }
    }

    public LRUCache(int capacity){
        this.capacity = capacity;
        this.size = 0;
        head = new DNodeList();
        tail = new DNodeList();
        head.next = tail;
        tail.prev = head;
    }


    public int get(int key){
        DNodeList node = cache.get(key);
        if(node == null){
            return -1;
        }
        moveToHead(node);
        return node.value;
    }

    public void put(int key,int value){
        DNodeList node = cache.get(key);
        if(node == null){
            DNodeList newNode = new DNodeList(key,value);
            addHead(newNode);
            cache.put(key,newNode);
            ++size;
            if(size>capacity){
                DNodeList oldTail = removeTail();
                cache.remove(oldTail.key);
                --size;
            }
        }else {
            node.value = value;
            moveToHead(node);
        }
    }

    public void moveToHead(DNodeList node){
        //先删除
       removeNode(node);
       //在添加到头部
        addHead(node);
    }

    /**
     * 双向链表移除节点
     * @param node
     */
    public void removeNode(DNodeList node){
        node.next.prev = node.prev;
        node.prev.next = node.next;
    }

    /**
     * 添加到头部
     * @param node
     */
    public void addHead(DNodeList node){
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    public void print(){
        DNodeList temp = head.next;
        while (temp.next != null){
            System.out.println(temp.value);
            temp = temp.next;
        }
    }

    /**
     * 移除尾部
     */
    public DNodeList removeTail(){
        DNodeList oldTail = tail.prev;
        removeNode(oldTail);
        return oldTail;
    }

    public static void main(String[] args) {
        LRUCache lruCache = new LRUCache(5);
        lruCache.put(1,1);
        lruCache.put(2,2);
        lruCache.put(3,3);
        lruCache.put(4,4);
        lruCache.put(5,5);
        lruCache.print();
        lruCache.get(3);
        System.out.println();
        lruCache.print();
    }

}
