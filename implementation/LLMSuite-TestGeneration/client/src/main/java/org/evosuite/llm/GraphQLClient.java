package org.evosuite.llm;
import com.apollographql.apollo3.ApolloCall;
import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.api.ApolloResponse;
import com.apollographql.apollo3.network.http.DefaultHttpEngine;
import com.apollographql.apollo3.rx3.Rx3Apollo;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Single;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.evosuite.Properties;
import org.evosuite.llm.type.Response;
import org.evosuite.utils.LoggingUtils;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class GraphQLClient {
    ApolloClient client;

    public GraphQLClient(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES).readTimeout(10, TimeUnit.MINUTES)
                .build();

        ApolloClient.Builder builder = new ApolloClient.Builder()
                .serverUrl(Properties.LLM_ENDPOINT);

        client = builder.httpEngine(new DefaultHttpEngine(okHttpClient)).build();
    }

    public Single<ApolloResponse<PromptQuery.Data>> promptQuery(String testcase){
        PromptQuery promptQuery = PromptQuery.builder().test(testcase).build();

        return Rx3Apollo.single(client.query(promptQuery));
    }

    public Single<ApolloResponse<PromptQuery.Data>> promptImproveVariableQuery(String testcase){
        PromptQuery promptQuery = PromptQuery.builder().test(testcase).type("variable").build();

        return Rx3Apollo.single(client.query(promptQuery));
    }

    public Single<ApolloResponse<PromptQuery.Data>> promptSuggestTestNameQuery(String testcase){
        PromptQuery promptQuery = PromptQuery.builder().test(testcase).type("testname").build();

        return Rx3Apollo.single(client.query(promptQuery));
    }

    public Single<ApolloResponse<PromptQuery.Data>> promptImproveTestDataQuery(String testcase){
        PromptQuery promptQuery = PromptQuery.builder().test(testcase).type("testdata").build();

        return Rx3Apollo.single(client.query(promptQuery));
    }

    public Single<ApolloResponse<PromptQuery.Data>> promptPoolQuery(String javadoc){
        PromptQuery promptQuery = PromptQuery.builder().test("").additionalParam(javadoc).type("pool").build();

        return Rx3Apollo.single(client.query(promptQuery));
    }

    public Single<ApolloResponse<PromptQuery.Data>> promptGenerateTestCaseQuery(String method){
        PromptQuery promptQuery = PromptQuery.builder().test(method).type("generate").build();

        return Rx3Apollo.single(client.query(promptQuery));
    }

    public Single<ApolloResponse<PromptQuery.Data>> promptGenerateCodaMosaTestCaseQuery(String method){
        PromptQuery promptQuery = PromptQuery.builder().test(method).type("codamosa").build();

        return Rx3Apollo.single(client.query(promptQuery));
    }
}
