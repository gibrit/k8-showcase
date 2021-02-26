package org.saltuk.users;

import io.vertx.core.Vertx;
import org.saltuk.core.ApiServer;
import org.saltuk.core.KubernetesManager;
import org.saltuk.users.table.UsersTable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author saltuk
 */
public class ServiceRunner {

    private static final String K8_DEBUG_TOKEN = "eyJhbGciOiJSUzI1NiIsImtpZCI6IlBvLTNybHJEU2prZlBlZlF2SXA4bmhQc0NudTdjcEVEZjB3QUZ4UmZGbXMifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJkZWZhdWx0LXRva2VuLXc1N3F3Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImRlZmF1bHQiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI5ODMzNTEzNC05NDNkLTQ3MmMtYWQ0Ni01YmNhOTcwNTEwNjgiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZS1zeXN0ZW06ZGVmYXVsdCJ9.OhxPjXHsBa6OmPhC1XqtjH9AIER3NEtNBYbyixx9N_eMvySlns8HpmoTrB8gj8DTX9hA-01IkbfzRmoh630o8ba8egj-VW7WDSH3QnKWBVJmF-7nT0063CdFrks2_sGlZ5KZfvTCCKCrybzSyjMPuBu0MRnMP799W6AO6z0lwBkayro84sAYsMhsAx4zoANHQCEY9pKNyFKkAIDBQuJlcAPGZ95RL5Ta5nxTg3SpIBOP1zzcexNrdJ3CmIfoLZFJJy4HF8i_FdtPq91xxQB4WJzPuVYYGB43OtZVUILsU4ODs0N7Q3D2OJ-4RwAZ7SzGAThLJiybSbtW-ke7LtD4OA";
    private static final String K8_HOST = "127.0.0.1";
    private static final int K8_PORT = 16443;
    private static final boolean DEBUG =false;
    private static final boolean K8_SSL = true;

    public static void main(String[] args) {
       KubernetesManager instance= DEBUG  ? KubernetesManager.kubernetesDebugInstance(K8_DEBUG_TOKEN, K8_HOST, K8_PORT, K8_SSL) : KubernetesManager.newInstance();
            instance.vertx(readVertx -> {
                if (readVertx.succeeded()) {
                    Vertx result = readVertx.result();
                    ApiServer apiServer = ApiServer.api(result, "user-service", UsersTable.class);
                    apiServer.listen(8080);
                } else {
                    throw new RuntimeException("Couldn't Find Clustered Vertx");
                }
            });
     
    }
}
