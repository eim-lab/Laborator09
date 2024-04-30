package ro.pub.cs.systems.eim.lab08.chatserviceandroidnsd.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import ro.pub.cs.systems.eim.lab08.chatserviceandroidnsd.R;
import ro.pub.cs.systems.eim.lab08.chatserviceandroidnsd.controller.NetworkServiceAdapter;
import ro.pub.cs.systems.eim.lab08.chatserviceandroidnsd.general.Constants;
import ro.pub.cs.systems.eim.lab08.chatserviceandroidnsd.networkservicediscoveryoperations.NetworkServiceDiscoveryOperations;

public class ChatNetworkServiceFragment extends Fragment {

    private EditText servicePortEditText = null;

    private Button serviceRegistrationStatusButton = null;
    private Button serviceDiscoveryStatusButton = null;

    private ChatActivity chatActivity = null;
    private NetworkServiceDiscoveryOperations networkServiceDiscoveryOperations = null;

    private View view = null;

    private NetworkServiceAdapter discoveredServicesAdapter = null;
    private NetworkServiceAdapter conversationsAdapter = null;

    private final ServiceRegistrationStatusButtonListener serviceRegistrationStatusButtonListener = new ServiceRegistrationStatusButtonListener();

    private class ServiceRegistrationStatusButtonListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            if (chatActivity.getServiceRegistrationStatus()) {
                String port = servicePortEditText.getText().toString();
                if (port.isEmpty()) {
                    Toast.makeText(getActivity(), "Field service port should be filled!", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    networkServiceDiscoveryOperations.registerNetworkService(Integer.parseInt(port));
                } catch (Exception exception) {
                    Log.e(Constants.TAG, "Could not register network service: " + exception.getMessage());
                    return;
                }
                startServiceRegistration();
            } else {
                networkServiceDiscoveryOperations.unregisterNetworkService();
                stopServiceRegistration();
            }
            chatActivity.setServiceRegistrationStatus(chatActivity.getServiceRegistrationStatus());
        }

    }

    private final ServiceDiscoveryStatusButtonListener serviceDiscoveryStatusButtonListener = new ServiceDiscoveryStatusButtonListener();

    private class ServiceDiscoveryStatusButtonListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            if (chatActivity.getServiceDiscoveryStatus()) {
                ((ChatActivity) getActivity()).getNetworkServiceDiscoveryOperations().startNetworkServiceDiscovery();
                startServiceDiscovery();
            } else {
                networkServiceDiscoveryOperations.stopNetworkServiceDiscovery();
                stopServiceDiscovery();
            }
            chatActivity.setServiceDiscoveryStatus(chatActivity.getServiceDiscoveryStatus());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        Log.v(Constants.TAG, "ChatNetworkServiceFragment -> onCreateView() callback method was invoked");

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_chat_network_service, parent, false);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        Log.v(Constants.TAG, "ChatNetworkServiceFragment -> onActivityCreated() callback method was invoked");

        servicePortEditText = getActivity().findViewById(R.id.port_edit_text);

        serviceRegistrationStatusButton = getActivity().findViewById(R.id.service_registration_status_button);
        serviceRegistrationStatusButton.setOnClickListener(serviceRegistrationStatusButtonListener);

        serviceDiscoveryStatusButton = getActivity().findViewById(R.id.service_discovery_status_button);
        serviceDiscoveryStatusButton.setOnClickListener(serviceDiscoveryStatusButtonListener);

        chatActivity = (ChatActivity) getActivity();
        networkServiceDiscoveryOperations = chatActivity.getNetworkServiceDiscoveryOperations();

        ListView discoveredServicesListView = getActivity().findViewById(R.id.discovered_services_list_view);
        discoveredServicesAdapter = new NetworkServiceAdapter(chatActivity, chatActivity.getDiscoveredServices());
        discoveredServicesListView.setAdapter(discoveredServicesAdapter);

        ListView conversationsListView = getActivity().findViewById(R.id.conversations_list_view);
        conversationsAdapter = new NetworkServiceAdapter(chatActivity, chatActivity.getConversations());
        conversationsListView.setAdapter(conversationsAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void startServiceRegistration() {
        serviceRegistrationStatusButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
        serviceRegistrationStatusButton.setText(getContext().getResources().getString(R.string.unregister_service));
    }

    public void stopServiceRegistration() {
        serviceRegistrationStatusButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRed));
        serviceRegistrationStatusButton.setText(getContext().getResources().getString(R.string.register_service));
    }

    public void startServiceDiscovery() {
        serviceDiscoveryStatusButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
        serviceDiscoveryStatusButton.setText(getContext().getResources().getString(R.string.stop_service_discovery));
    }

    public void stopServiceDiscovery() {
        serviceDiscoveryStatusButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRed));
        serviceDiscoveryStatusButton.setText(getContext().getResources().getString(R.string.start_service_discovery));
    }


    public NetworkServiceAdapter getDiscoveredServicesAdapter() {
        return discoveredServicesAdapter;
    }

    public NetworkServiceAdapter getConversationsAdapter() {
        return conversationsAdapter;
    }

}
