package com.shopping.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.shopping.entity.Product;
import com.shopping.entity.ShoppingCar;
import com.shopping.entity.ShoppingRecord;
import com.shopping.entity.User;
import com.shopping.service.ProductService;
import com.shopping.service.ShoppingCarService;
import com.shopping.service.ShoppingRecordService;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sun.rmi.runtime.Log;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by 14437 on 2017/3/1.
 */
@Controller
public class ProductController {
    private static String pname="";

    @Resource
    private ShoppingCarService shoppingCarService;
    @Resource
    private ShoppingRecordService shoppingRecordService;
    @Resource
    private ProductService productService;

    @RequestMapping(value = "/getAllProducts")
    @ResponseBody
    public Map<String,Object> getAllProducts(){
        List<Product> productList = new ArrayList<>();
        productList = productService.getAllProduct();
        String allProducts = JSONArray.toJSONString(productList);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("allProducts",allProducts);
        return resultMap;
    }


    @RequestMapping(value = "/getAllProductsandRecomand")
    @ResponseBody
    public Map<String,Object> getAllProductsandRecomand(int userId){
/**
 * 1.通过模糊匹配算法，实现数据的匹配与解析。
 2.	基于内容的推荐，根据类型和商品的详细信息进行解析、录入，进行匹配推荐。
 3.	基于时间的推荐，根据时间的算法，匹配季节的信息，进行推荐。
 4.	基于地理位置的推荐，根据所处的地理位置进行算法匹配。
 5.	基于热度的推荐，进行推荐。

 */
        System.out.println("用户的id："+userId);
        List<Product> productList = new ArrayList<>();
        List<Product> productTem = new ArrayList<>();
        List<Product> productRcommand = new ArrayList<>();
        productList = productService.getAllProduct();

        if (userId!=0) {
            /**
             * 获得用户最近浏览的信息
             */
            List<ShoppingCar> shoppingCarList = shoppingCarService.getShoppingCars(userId);
            List<ShoppingRecord> shoppingRecordList = shoppingRecordService.getShoppingRecords(userId);

            /**
             * 对信息进行筛选，获得需要的信息
             */
            if (shoppingCarList.size()>0){
                Product pp = productService.getProduct(shoppingCarList.get(0).getProductId());
                productRcommand.add(pp);
            }
            if (shoppingRecordList.size()>0&&shoppingCarList.size()<0){
                Product pp = productService.getProduct(shoppingRecordList.get(0).getProductId());
                productRcommand.add(pp);
            }else if (shoppingRecordList.size()>0&&shoppingCarList.size()>0){
                if (shoppingCarList.get(0).getProductId()!=shoppingRecordList.get(0).getProductId()){
                    Product pp = productService.getProduct(shoppingRecordList.get(0).getProductId());
                    productRcommand.add(pp);
                }else if (shoppingRecordList.size()>1){
                    Product pp = productService.getProduct(shoppingRecordList.get(1).getProductId());
                    productRcommand.add(pp);
                }
            }

            /**
             * 得到所有推荐的信息
             *
             * 提取关键词、提取需要的信息
             */
            if (productRcommand.size()>0){
                for (int i=0;i<productRcommand.size();i++){
                    String namet=productRcommand.get(i).getName();
                    String namee=namet;
                    if (namet.length()>3){
                         namee=namet.substring(0,3);
                    }else {
                         namee=namet;
                    }

                    String descrip=productRcommand.get(i).getDescription().split(",")[0];
                    int typee=productRcommand.get(i).getType();
                    String words=productRcommand.get(i).getKeyWord().split(";")[0];

                    /**
                     * 按照商品的类型进行推荐
                     */

                    List<Product> ppt =productService.getProductsByType(typee);
                    if (ppt.size()>0){

                        Product ppv=ppt.get(0);
                        if (ppv!=null&&ppv.getName()!=null&&!ppv.getName().equals("")) {
                            ppv.setType(8);
                            productTem.add(ppv);
                        }
                    }


                    /**
                     * 进行模糊匹配算法，之后进行筛选
                     */
                    List<Product> ppw =productService.getProductsByKeyWord(""+words);
                    if (ppw.size()>0){
                        Product ppv=ppw.get(0);
                        if (ppv!=null&&ppv.getName()!=null&&!ppv.getName().equals("")) {
                            ppv.setType(8);
                            productTem.add(ppv);
                        }
                    }

                    /**
                     * 按照浏览的记录信息，确定所属的种类，获得在每个季节使用的商品，根据名称进行筛选，获取
                     */
                    Product ppn =productService.getProduct(""+namee);
                    if (ppn!=null&&ppn.getName()!=null&&!ppn.getName().equals("")){
                        Product ppv=ppn;
                        if (ppv!=null&&ppv.getName()!=null&&!ppv.getName().equals("")) {
                            ppv.setType(8);
                            productTem.add(ppv);
                        }
                    }




                    /**
                     * 按照商品的内容和描述进行解析，然后进行匹配推荐
                     */
                    List<Product> ppd =productService.getProductsByKeyWord(""+descrip);
                    if (ppd.size()>0){
                        Product ppv=ppd.get(0);
                        if (ppv!=null&&ppv.getName()!=null&&!ppv.getName().equals("")) {
                            ppv.setType(8);
                            productTem.add(ppv);
                        }
                    }






                }
            }


            /**
             * 按照商品的流行度进行分析，获得需要的信息，选出最有可能的商品
             */

            int popul[]=new int[9];
            for (int i=0;i<popul.length;i++){
                popul[i]=0;
            }
            for (int j=0;j<productList.size();j++){
                if (productList.get(j).getType()==1){
                    popul[0]+=1;
                }else if (productList.get(j).getType()==2){
                    popul[1]+=1;
                }else if (productList.get(j).getType()==3){
                    popul[2]+=1;
                }else if (productList.get(j).getType()==4){
                    popul[3]+=1;
                }else if (productList.get(j).getType()==5){
                    popul[4]+=1;
                }else if (productList.get(j).getType()==6){
                    popul[5]+=1;
                }else if (productList.get(j).getType()==7){
                    popul[6]+=1;
                }
            }
            int tag=0;
            for (int n=0;n<7;n++){
                if (popul[n]>=tag){
                    tag=n;
                }
            }
            Product popluar=null;
            for (int h=0;h<productList.size();h++){
                if (productList.get(h).getType()==tag){
                    Product pt=productList.get(h);
                    boolean tt=false;
                    for (int r=0;r<productTem.size();r++){
                        if (productTem.get(r).getId()!=pt.getId()){
                            tt=true;
                            popluar=pt;
                            break;
                        }
                    }
                }
            }


            for (int u=0;u<productTem.size();u++){
                productList.add(productTem.get(u));
            }
            if (popluar!=null&&popluar.getName()!=null){
                productList.add(popluar);
            }



        }

        /**
         * 对信息进行封装
         */
        String allProducts = JSONArray.toJSONString(productList);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("allProducts",allProducts);
        return resultMap;
    }


    @RequestMapping(value = "/deleteProduct", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deleteProduct(int id) {
        String result ="fail";
        if(productService.deleteProduct(id)){
            result="success";
        }
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("result",result);
        return resultMap;
    }

    @RequestMapping(value = "/addProduct", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addProduct(String name,String description,String keyWord,int price,int counts,int type) {
        System.out.println("添加了商品："+name);
        pname=name;
        String result ="fail";
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setKeyWord(keyWord);
        product.setPrice(price);
        product.setCounts(counts);
        product.setType(type);
        productService.addProduct(product);
        result = "success";
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("result",result);
        return resultMap;
    }

    @RequestMapping(value = "/productDetail", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> productDetail(int id, HttpSession httpSession) {
        System.out.println("I am here!"+id);
        Product product = productService.getProduct(id);
        httpSession.setAttribute("productDetail",product);
        System.out.print("I am here"+product.getName());
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("result","success");
        return resultMap;
    }

    @RequestMapping(value = "/product_detail")
    public String product_detail() {
        return "product_detail";
    }

    @RequestMapping(value = "/searchPre", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> searchPre(String searchKeyWord,HttpSession httpSession) {
        httpSession.setAttribute("searchKeyWord",searchKeyWord);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("result","success");
        return resultMap;
    }

    @RequestMapping(value = "/search")
    public String search() {
        return "search";
    }

    @RequestMapping(value = "/searchProduct", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> searchProduct(String searchKeyWord){
        System.out.println("我到了SearchProduct"+searchKeyWord);
        List<Product> productList = new ArrayList<Product>();
        productList = productService.getProductsByKeyWord(searchKeyWord);
        String searchResult = JSONArray.toJSONString(productList);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("result",searchResult);
        System.out.println("我返回了"+searchResult);
        return resultMap;
    }

    @RequestMapping(value = "/getProductById", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getProductById(int id) {
        Product product = productService.getProduct(id);
        String result = JSON.toJSONString(product);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("result",result);
        return resultMap;
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadFile(@RequestParam MultipartFile productImgUpload,String name, HttpServletRequest request) {
        String result = "fail";
        try{
            System.out.println(pname+"shagnp"+name);
            if(productImgUpload != null && !productImgUpload.isEmpty()) {
                //F:\IdeaProjects\ShopRar\Shopping-master\webapp\static\img\55.jpg
                //String fileRealPath = request.getSession().getServletContext().getRealPath("/static/img");
                String fileRealPath = "F:\\IdeaProjects\\ShopRar\\Shopping-master\\src\\main\\webapp\\static\\img\\";
                int id = productService.getProduct(pname).getId();
                String fileName = String.valueOf(id)+".jpg";
                File fileFolder = new File(fileRealPath);
                System.out.println("fileRealPath=" + fileRealPath+"\\"+fileName);
                if(!fileFolder.exists()){
                    fileFolder.mkdirs();
                }
                File file = new File(fileFolder,fileName);
                productImgUpload.transferTo(file);
                result = "success";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("result",result);
        return resultMap;
    }
}

