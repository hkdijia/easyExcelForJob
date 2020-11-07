package com.alibaba.easyexcel.test.demo.read;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 假设这个是你的DAO存储。当然还要这个类让spring管理，当然你不用需要存储，也不需要这个类。
 *
 * @author Jiaju Zhuang
 **/
public class DemoDAO {

    private static String foldNamePre = "H:\\对外援助\\菜菜\\原始文件copy\\市刀剪厂PDF\\";

    public void save(List<DemoData> list) {
        // 如果是mybatis,尽量别直接调用多次insert,自己写一个mapper里面新增一个方法batchInsert,所有数据一次性插入

        List<String> archiveNumList = list.stream()
                                            .map(DemoData::getArchiveNum).distinct()
                                            .collect(Collectors.toList());

        System.out.println("当前100条数据内案卷档号为： "+ archiveNumList);
        System.out.println("当前100条数据内案卷总数为：" + archiveNumList.size());

        // 对 list 进行区分和统计
        for (String archiveNum : archiveNumList) {
            // 拿到此案卷下 所有卷内文件夹名称
            List<String> folderNames = this.ergodicPage(archiveNum);

            for (DemoData demoData : list) {
                // 案卷名称匹配后进行数据交换
                if(archiveNum.equals(demoData.getArchiveNum())){
                    // 匹配卷内的文件夹名称  (卷内)
                    if(folderNames.contains(demoData.getOrderNum())){
                        // 进行重命名
                        String trueName = demoData.getFileNum();
                        //String pageNum = demoData.getPageNum();
                        String orderNum = demoData.getOrderNum();
                        this.rename(trueName,demoData.getArchiveNum(),orderNum);
                    }

                }
            }
        }
    }

    private void rename(String trueName, String archiveNum,String pageNum) {
        StringBuilder fullName = new StringBuilder(foldNamePre)
                                                .append(archiveNum)
                                                .append("\\")
                                                .append(pageNum);


        //String fullName = foldNamePre + archiveNum  + pageNum;
        File file = new File(fullName.toString());
        File[] listFiles = file.listFiles();
        // 执行重命名逻辑
        if(listFiles.length > 0){
            for (File smallFile : listFiles) {
                String[] split = smallFile.getName().split("\\.");
                String ext = split[split.length - 1];

                StringBuilder newName = new StringBuilder(foldNamePre)
                                            .append(archiveNum)
                                            .append("\\")
                                            .append(pageNum)
                                            .append("\\")
                                            .append(trueName)
                                            .append(".")
                                            .append(ext);
                File target = new File(newName.toString());
                smallFile.renameTo(target);

            }
        }
    }

    // 遍历文件夹名称
    public List<String> ergodicPage(String folderName){
        List<String> list = new ArrayList<>();
        String fullName = foldNamePre + folderName;
        File file = new File(fullName);
        File[] listFiles = file.listFiles();
        if(listFiles.length > 0){
            for (File smallFile : listFiles) {
                // 遍历这个目录下的 全部文件夹
                if(smallFile.isDirectory()){
                    String name = smallFile.getName();
                    System.out.println("案卷下 卷内的名称：" + name);
                    String path = smallFile.getPath();
                    list.add(name);
                }
            }
        }
        return list;
    }
}
