package fredine.reactive.controller;

import com.intuit.ipp.data.CompanyInfo;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.QueryResult;
import com.intuit.ipp.util.Config;
import com.intuit.oauth2.exception.OAuthException;
import fredine.reactive.client.DataServiceFactory;
import fredine.reactive.client.TokenRefreshingDataServiceAdapter;
import fredine.reactive.client.TokenStore;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@Controller("/")
public class QBOController {

	@Value("${intuit.accounting.api.host}")
	private String intuitAccountingAPIHost;

	@Inject
	private
    DataServiceFactory factory;
	
	private static final Logger logger = LoggerFactory.getLogger(QBOController.class);

    @Get("/getCompanyInfo")
    public CompanyInfo callQBOCompanyInfo(TokenStore tokenStore) throws FMSException, OAuthException {
    	String url =  intuitAccountingAPIHost + "/v3/company";
        TokenRefreshingDataServiceAdapter service = factory.getDataService(tokenStore);

        Config.setProperty(Config.BASE_URL_QBO, url);

        String sql = "select * from companyinfo";
        QueryResult queryResult = service.executeQuery(sql);
        return processResponse(queryResult);
    }

    private CompanyInfo processResponse(QueryResult queryResult) {
        if (!queryResult.getEntities().isEmpty() && queryResult.getEntities().size() > 0) {
            CompanyInfo companyInfo = (CompanyInfo) queryResult.getEntities().get(0);
            logger.info("Companyinfo -> CompanyName: " + companyInfo.getCompanyName());
            return companyInfo;
        }
        return null;
    }
}
