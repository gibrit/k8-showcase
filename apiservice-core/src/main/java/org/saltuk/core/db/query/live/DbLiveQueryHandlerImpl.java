/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.saltuk.core.db.query.live;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import java.util.regex.Pattern;
import org.saltuk.core.stream.StreamClient;

/**
 *
 * @author saltuk
 */
class DbLiveQueryHandlerImpl implements DbLiveQueryHandler {

    private final PermittedOptions liveAdressPermission;
    private static final Pattern UID_PATTERN = Pattern.compile("api\\.live\\.([0-9A-z_]+)");
    private final Vertx vertx;

    public DbLiveQueryHandlerImpl(Vertx vertx) {
        this.vertx = vertx;
        this.liveAdressPermission = new PermittedOptions().setAddressRegex("api\\.live\\.([0-9A-z_]+)");
    }

    @Override
    public Router handler(SockJSHandlerOptions options) {
        BridgeOptions bridge = new BridgeOptions();
        bridge.addOutboundPermitted(liveAdressPermission);
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx, options);
        return sockJSHandler.bridge(bridge, execute -> {

            if (execute.type() == BridgeEventType.REGISTER || execute.type() == BridgeEventType.UNREGISTER) {
                String address = execute.getRawMessage().getString("address");
                if (UID_PATTERN.matcher(address).matches()) {
                    final StreamClient client = StreamClient.create(vertx, address);
                    String token = execute.getRawMessage().getJsonObject("headers", new JsonObject()).getString("token");
                    if (execute.type() == BridgeEventType.REGISTER) {
                        client.checkState(token, checkState -> {
                            if (checkState.succeeded()) {
                                if (checkState.result()) {
                                    client.connect(token, doConnect -> {
                                        execute.handle(Future.succeededFuture(true));
                                    });
                                } else {
                                    execute.handle(Future.succeededFuture(false));
                                }
                            } else {
                                execute.handle(Future.failedFuture(checkState.cause()));
                            }
                        });
                    } else {
                        client.disconnect(token, doDisconnect -> {
                            execute.handle(Future.succeededFuture(true));
                        });
                    }
                } else {
                    execute.handle(Future.succeededFuture(true));
                }
            } else {
                execute.handle(Future.succeededFuture(true));
            }
        });

    }

}
