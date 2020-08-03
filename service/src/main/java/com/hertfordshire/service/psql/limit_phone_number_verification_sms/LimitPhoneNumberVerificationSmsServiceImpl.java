package com.hertfordshire.service.psql.limit_phone_number_verification_sms;

import com.hertfordshire.dao.psql.AdminSettingsDao;
import com.hertfordshire.dao.psql.LimitPhoneNumberVerificationSmsDao;
import com.hertfordshire.model.psql.AdminSettings;
import com.hertfordshire.model.psql.LimitPhoneNumberVerificationSms;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LimitPhoneNumberVerificationSmsServiceImpl implements LimitPhoneNumberVerificationSmsService {

    @Autowired
    private LimitPhoneNumberVerificationSmsDao limitPhoneNumberVerificationSmsDao;

    @Autowired
    private AdminSettingsDao adminSettingsDao;

    @Override
    public boolean saveOrUpdate(PortalUser portalUser) {

        AdminSettings adminSetting;
        List<AdminSettings> adminSettingsList = adminSettingsDao.findAll();
        adminSetting = adminSettingsList.stream().findFirst().orElse(null);

        if (adminSetting != null) {
            LimitPhoneNumberVerificationSms limitPhoneNumberVerificationSms = null;

            limitPhoneNumberVerificationSms = this.limitPhoneNumberVerificationSmsDao.findByOwner(portalUser);
            if (limitPhoneNumberVerificationSms != null) {

                if (limitPhoneNumberVerificationSms.getCount() >= adminSetting.getAccountVerificationSmsCount()) {
                    long hours = Utils.getDifferenceBetweenTwoDates(limitPhoneNumberVerificationSms.getDateUpdated());
                    if (hours >= adminSetting.getAfterHoursPermitSendingOfSms()) {
                        limitPhoneNumberVerificationSms.setCount(0);
                        Date date = new Date();
                        limitPhoneNumberVerificationSms.setDateUpdated(date);
                        limitPhoneNumberVerificationSmsDao.save(limitPhoneNumberVerificationSms);
                        return true;
                    }
                    return false;
                } else {
                    int value = limitPhoneNumberVerificationSms.getCount();
                    limitPhoneNumberVerificationSms.setCount(value + 1);
                    this.limitPhoneNumberVerificationSmsDao.save(limitPhoneNumberVerificationSms);
                    return true;
                }

            } else {
                limitPhoneNumberVerificationSms = new LimitPhoneNumberVerificationSms();
                limitPhoneNumberVerificationSms.setOwner(portalUser);
                limitPhoneNumberVerificationSms.setCount(1);
                this.limitPhoneNumberVerificationSmsDao.save(limitPhoneNumberVerificationSms);
                return true;
            }
        }
        return true;
    }
}
