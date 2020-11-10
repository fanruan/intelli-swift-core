//package com.fr.swift.executor.task.netty.client;
//
//
//import com.fr.swift.executor.task.netty.protocol.FilePacket;
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//
//import java.io.RandomAccessFile;
//
///**
// * @author Hoky
// * @date 2020/11/6
// */
//@ChannelHandler.Sharable
//public class FileUploadClientHandler extends ChannelInboundHandlerAdapter {
//
//    private int byteRead;
//    private volatile Long start = 0l;   //使用Long 当传输的文件大于2G时，Integer类型会不够表达文件的长度
//    private volatile int lastLength = 0;
//    public RandomAccessFile randomAccessFile;
//    private FilePacket filePacket;
//
////    public static void main(String[] args) {
////        int port = 8123;
////        if (args != null && args.length > 0) {
////            port = Integer.valueOf(args[0]);
////        }
////
////        FilePacket filePacket = new FilePacket();
////        File file = new File("/Users/hoky/Work/fanruan/code/swift-gc/target/cubes/202010.zip");
////        String fileMd5 = file.getName();
////        filePacket.setFile(file);
////        filePacket.setFile_md5(fileMd5);
////        filePacket.setStartPos(0);     //要传输的文件的初始信息
////
////        new FileUploadClient().connect("127.0.0.1", port, filePacket);
////    }
//
//    //构造器，FilePacket作为参数
//    public FileUploadClientHandler(FilePacket filePacket) {
//        if (filePacket.getFile().exists()) {
//            if (!filePacket.getFile().isFile()) {
//                System.out.println("Not a file:" + filePacket.getFile());
//            }
//        }
//        this.filePacket = filePacket;
//    }
//
//    @Override    //当前channel激活的时候的时候触发  优先于channelRead方法执行  （我的理解，只执行一次）
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        randomAccessFile = new RandomAccessFile(filePacket.getFile(), "r");
//        randomAccessFile.seek(filePacket.getStartPos());
//        lastLength = Integer.MAX_VALUE / 4;   //每次发送的文件块数的长度
//        byte[] bytes = new byte[lastLength];
//
//        if ((byteRead = randomAccessFile.read(bytes)) != -1) {
//            filePacket.setEndPos(byteRead);
//            filePacket.setBytes(bytes);
//            ctx.writeAndFlush(filePacket);
//        } else {
//            System.out.println("文件已读完");
//        }
//    }
//
//    @Override  //当前channel从远端读取到数据时触发
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        if (msg instanceof Long) {    //客户端发送FilePacket 到服务端，服务端处理完文件当前部分的数据，返回下次文件段的起始位置
//            start = (Long) msg;
//            if (start != -1) {
//                randomAccessFile = new RandomAccessFile(filePacket.getFile(), "r");
//                randomAccessFile.seek(start);  //将服务端返回的数据设置此次读操作，文件的起始偏移量
//                System.out.println("文件长度：" + (randomAccessFile.length()));
//                System.out.println("此次分片开始位置：" + start);
//                System.out.println("块儿长度：" + (Integer.MAX_VALUE / 4));
//                System.out.println("剩余长度：" + (randomAccessFile.length() - start));
//                Long a = randomAccessFile.length() - start;
//                int lastLength = (int) (Integer.MAX_VALUE / 4);
//                if (a < lastLength) {
//                    System.out.println("最后一片长度：" + a);
//                    lastLength = a.intValue();
//                }
//
//                byte[] bytes = new byte[lastLength];
//
//                //这个判断关闭判断调整存在漏洞
//                if ((byteRead = randomAccessFile.read(bytes)) != -1 && (randomAccessFile.length() - start) > 0) {
//                    System.out.println("byte长度：" + bytes.length);
//                    System.out.println("-----------------" + bytes.length);
//                    filePacket.setEndPos(byteRead);
//                    filePacket.setBytes(bytes);
//                    ctx.writeAndFlush(filePacket);
//                } else {
//                    randomAccessFile.close();
//                    ctx.close();
//                    System.out.println("文件已读完------" + byteRead);
//                }
//
//            }
//        }
//    }
//
//    @Override  //在当前ChannelHandler回调方法出现异常时被回调
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        cause.printStackTrace();
//        ctx.close();
//    }
//
//}
