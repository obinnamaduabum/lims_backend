package com.hertfordshire.service.resolvers;

import com.google.gson.Gson;
import com.hertfordshire.model.psql.LabTestOrderDetail;
import com.hertfordshire.model.psql.OrdersModel;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.pojo.LabTestDetailsPojo;
import com.hertfordshire.pojo.LabTestOrdersPojo;
import com.hertfordshire.pojo.PortalUserPojo;
import com.hertfordshire.dao.psql.LabTestOrderDetailDao;
import com.hertfordshire.service.psql.lab_test.LabTestService;
import com.hertfordshire.service.psql.lab_test_order_details.LabTestOrderDetailsService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.service.psql.test_order.TestOrderService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.Utils;
import com.hertfordshire.utils.lhenum.CurrencyTypeConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDetailsAndSampleCollectionStatusQuery {

    private final Logger logger = LoggerFactory.getLogger(OrderDetailsAndSampleCollectionStatusQuery.class.getSimpleName());


    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private Gson gson;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private LabTestService labTestService;

    @Autowired
    private LabTestOrderDetailsService labTestOrderDetailsService;

    @Autowired
    private com.hertfordshire.pubsub.redis.service.portal_user.PortalUserService redisPortalUserService;


    @Autowired
    private LabTestOrderDetailDao labTestOrderDetailDao;

    @Autowired
    private TestOrderService orderIdService;

    public LabTestOrdersPojo fetchOrderModel(Long id) {

        try {
            Optional<OrdersModel> ordersModelOptional = this.orderIdService.findById(id);

            if (ordersModelOptional.isPresent()) {

                OrdersModel ordersModel = ordersModelOptional.get();

                PortalUser portalUser = this.portalUserService.findById(ordersModel.getPortalUser().getId());

                PortalUserPojo portalUserPojo = new PortalUserPojo();
                portalUserPojo.setFirstName(portalUser.getFirstName());
                portalUserPojo.setLastName(portalUser.getLastName());
                portalUserPojo.setCode(portalUser.getCode());
                portalUserPojo.setOtherName(portalUser.getOtherName());
                portalUserPojo.setPhoneNumber(portalUser.getPhoneNumber());

                LabTestOrdersPojo labTestOrdersPojo = new LabTestOrdersPojo();
                labTestOrdersPojo.setPortalUserPojo(portalUserPojo);
                labTestOrdersPojo.setId(ordersModel.getId());
                labTestOrdersPojo.setCurrencyType(ordersModel.getCurrencyType());
                labTestOrdersPojo.setCode(ordersModel.getCode());
                labTestOrdersPojo.setCashCollected(ordersModel.isCashCollected());
                labTestOrdersPojo.setDateCreated(ordersModel.getDateCreated());
                labTestOrdersPojo.setDateUpdated(ordersModel.getDateUpdated());

                if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.NGN)) {
                    labTestOrdersPojo.setPrice(Long.valueOf(Utils.koboToNaira(ordersModel.getPrice())));
                } else if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.USD)) {
                    labTestOrdersPojo.setPrice(ordersModel.getPrice());
                } else if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.EUR)) {
                    labTestOrdersPojo.setPrice(ordersModel.getPrice());
                }

                List<LabTestOrderDetail> labTestOrderDetails = labTestOrderDetailDao.findByOrdersModel(ordersModel);

                List<LabTestDetailsPojo> labTestDetailsPojos = new ArrayList<>();

                for (LabTestOrderDetail labTestOrderDetail : labTestOrderDetails) {

                    LabTestDetailsPojo labTestDetailsPojo = new LabTestDetailsPojo();
                    labTestDetailsPojo.setId(labTestOrderDetail.getId());
                    labTestDetailsPojo.setLabTestId(labTestOrderDetail.getLabTest().getId());
                    labTestDetailsPojo.setName(labTestOrderDetail.getName());
                    labTestDetailsPojo.setUniqueId(labTestOrderDetail.getUniqueId());

                    if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.NGN)) {
                        labTestDetailsPojo.setPrice(Long.valueOf(Utils.koboToNaira(labTestOrderDetail.getPrice())));
                    } else if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.USD)) {
                        labTestDetailsPojo.setPrice(labTestOrderDetail.getPrice());
                    } else if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.EUR)) {
                        labTestDetailsPojo.setPrice(labTestOrderDetail.getPrice());
                    }

                    labTestDetailsPojo.setQuantity(labTestOrderDetail.getQuantity());

                    if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.NGN)) {
                        labTestDetailsPojo.setTotal(Long.valueOf(Utils.koboToNaira(labTestOrderDetail.getTotal())));
                    } else if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.USD)) {
                        labTestDetailsPojo.setTotal(labTestOrderDetail.getTotal());
                    } else if (CurrencyTypeConstant.valueOf(ordersModel.getCurrencyType().name()).equals(CurrencyTypeConstant.EUR)) {
                        labTestDetailsPojo.setTotal(labTestOrderDetail.getTotal());
                    }


                    labTestDetailsPojos.add(labTestDetailsPojo);
                }

                labTestOrdersPojo.setLabTestDetailsPojos(labTestDetailsPojos);


                logger.info("found ++++");
                logger.info(this.gson.toJson(labTestOrdersPojo));

                return labTestOrdersPojo;

            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
