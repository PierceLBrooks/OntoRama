package ontorama.backends.p2p;

import net.jxta.impl.peergroup.StdPeerGroup;

//import java.util.*;
//import java.io.*;
//
////import org.apache.log4j.Logger;
////import org.apache.log4j.Level;
//
//import net.jxta.peergroup.PeerGroup;
//import net.jxta.peergroup.PeerGroupID;
//import net.jxta.protocol.*;
//import net.jxta.id.ID;
//import net.jxta.exception.*;
//
//import net.jxta.document.MimeMediaType;
//import net.jxta.document.Advertisement;
//import net.jxta.document.StructuredDocument;
//
//import net.jxta.impl.peergroup.Configurator;
//import net.jxta.impl.peergroup.StdPeerGroup;
//import net.jxta.impl.peergroup.StdPeerGroupParamAdv;
//import net.jxta.impl.config.Config;
//
///*
// * Copyright (c) 2001-2002 Sun Microsystems, Inc.  All rights
// * reserved.
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions
// * are met:
// *
// * 1. Redistributions of source code must retain the above copyright
// *    notice, this list of conditions and the following disclaimer.
// *
// * 2. Redistributions in binary form must reproduce the above copyright
// *    notice, this list of conditions and the following disclaimer in
// *    the documentation and/or other materials provided with the
// *    distribution.
// *
// * 3. The end-user documentation included with the redistribution,
// *    if any, must include the following acknowledgment:
// *       "This product includes software developed by the
// *       Sun Microsystems, Inc. for Project JXTA."
// *    Alternately, this acknowledgment may appear in the software itself,
// *    if and wherever such third-party acknowledgments normally appear.
// *
// * 4. The names "Sun", "Sun Microsystems, Inc.", "JXTA" and "Project JXTA" must
// *    not be used to endorse or promote products derived from this
// *    software without prior written permission. For written
// *    permission, please contact Project JXTA at http://www.jxta.org.
// *
// * 5. Products derived from this software may not be called "JXTA",
// *    nor may "JXTA" appear in their name, without prior written
// *    permission of Sun.
// *
// * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
// * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
// * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// * DISCLAIMED.  IN NO EVENT SHALL SUN MICROSYSTEMS OR
// * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
// * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
// * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
// * SUCH DAMAGE.
// * ====================================================================
// *
// * This software consists of voluntary contributions made by many
// * individuals on behalf of Project JXTA.  For more
// * information on Project JXTA, please see
// * <http://www.jxta.org/>.
// *
// * This license is based on the BSD license adopted by the Apache Foundation.
// *
// * $Id: Platform.java,v 1.88 2003/01/14 18:46:10 SeanKelly Exp $
// */
//

/**
 * @author nataliya
 */
public class OntoRamaPeerGroup  extends StdPeerGroup {





////	private static final Logger LOG = Logger.getLogger(Platform.class.getName());
//
//	Configurator config;
//
//	private static boolean initialized = false;
//	
//	private MimeMediaType mimeMediaType = new MimeMediaType( "text/xml;charset=\"UTF-8\"" );
//
//	public synchronized void init(PeerGroup nullParent, ID nullID, Advertisement nullAdv)
//											throws PeerGroupException {
//
//		if ( initialized ) {
////			if (LOG.isEnabledFor(Level.WARN))
////				LOG.warn("You cannot initialize more than one World PeerGroup!");
//			System.out.println("You cannot initialize more than one World PeerGroup!");
//			return;
//		}
//
//		initialized = true;
//
//		if (nullParent != null) {
////			if (LOG.isEnabledFor(Level.ERROR))
////				LOG.error("World PeerGroup cannot be instantiated with a parent group!");
//			System.out.println("World PeerGroup cannot be instantiated with a parent group!");
//		}
//
//		if (nullAdv != null) {
////			if (LOG.isEnabledFor(Level.WARN))
////				LOG.warn("World PeerGroup ignores passed in advertisement");
//			System.out.println("World PeerGroup ignores passed in advertisement");
//		}
//
//		config = new Configurator();
//
//		setConfigAdvertisement(generateConfigAdvertisement());
//
//		ModuleImplAdvertisement platformDef;
//		try {
//			// Build the platform's impl adv.
//			platformDef = mkPlatformImplAdv();
//		} catch (Exception e) {
//			throw new PeerGroupException(e.getMessage());
//		}
//
//		// Initialize the group.
//		super.init(null, PeerGroupID.worldPeerGroupID, platformDef);
//
//		try {
//			// Publish our own adv.
//			publishGroup("World PeerGroup",
//			"Standard World PeerGroup Reference Implementation");
//		} catch (IOException e) {
//			throw new PeerGroupException(e.getMessage());
//		}
//
//		// Clear the reconf flag since things seem to be working.
//		config.clearReconf();
//
//		// make sure the JXTA_HOME directory is present.  Defensive programming
//		// to make sure we dont' go forward while our directories aren't setup.
//		new File( Config.JXTA_HOME ).mkdirs();
//
//		return;
//	}
//
//	protected PeerAdvertisement generateConfigAdvertisement() throws PeerGroupException {
//		if (config.cancelPlatform())
//			throw new PeerGroupException("World PeerGroup creation canceled at user's request");
//
//		// We set reconf in advance, just in case something screws up, in
//		// which case we want the configurator to show-up by default.
//		config.setReconf();
//
//		return config.get();
//	}
//
//	protected ModuleImplAdvertisement mkPlatformImplAdv() throws Exception {
//
//		// Start building the implAdv for the platform intself.
//		ModuleImplAdvertisement platformDef =
//		mkImplAdvBuiltin(PeerGroup.refPlatformSpecID,
//				 "World PeerGroup",
//				 "Standard World PeerGroup Reference Implementation");
//
//		// Build the param section now.
//		StdPeerGroupParamAdv paramAdv = new StdPeerGroupParamAdv();
//		Hashtable protos = new Hashtable();
//		Hashtable services = new Hashtable();
//		Hashtable apps = new Hashtable();
//
//		// Build ModuleImplAdvs for each of the modules
//		ModuleImplAdvertisement moduleAdv;
//
//		// Do the Services
//
//		moduleAdv =
//				mkImplAdvBuiltin(refResolverSpecID,
//				"net.jxta.impl.resolver.ResolverServiceImpl",
//				"Reference Implementation of the ResolverService service");
//		services.put(resolverClassID, moduleAdv);
//
//		moduleAdv =
//				mkImplAdvBuiltin(refDiscoverySpecID,
//				"net.jxta.impl.discovery.DiscoveryServiceImpl",
//				"Reference Implementation of the DiscoveryService service");
//		services.put(discoveryClassID, moduleAdv);
//
//		moduleAdv =
//				mkImplAdvBuiltin(refPipeSpecID,
//				"net.jxta.impl.pipe.PipeServiceImpl",
//				"Reference Implementation of the PipeService service");
//		services.put(pipeClassID, moduleAdv);
//
//		moduleAdv =
//				mkImplAdvBuiltin(refMembershipSpecID,
//				"net.jxta.impl.membership.NullMembershipService",
//				"Reference Implementation of the MembershipService service");
//		services.put(membershipClassID, moduleAdv);
//
//		moduleAdv =
//				mkImplAdvBuiltin(refRendezvousSpecID,
//				"net.jxta.impl.rendezvous.RendezVousServiceImpl",
//				"Reference Implementation of the Rendezvous service");
//		services.put(rendezvousClassID, moduleAdv);
//
//		moduleAdv =
//				mkImplAdvBuiltin(refPeerinfoSpecID,
//				"net.jxta.impl.peer.PeerInfoServiceImpl",
//				"Reference Implementation of the Peerinfo service");
//		services.put(peerinfoClassID, moduleAdv);
//
//		moduleAdv =
//				mkImplAdvBuiltin(refProxySpecID,
//				"net.jxta.impl.proxy.ProxyService",
//				"Reference Implementation of the Proxy service");
//		services.put(proxyClassID, moduleAdv);
//
//		moduleAdv =
//				mkImplAdvBuiltin(refEndpointSpecID,
//				"net.jxta.impl.endpoint.EndpointServiceImpl",
//				"Reference Implementation of the EndpointService service");
//		services.put(endpointClassID, moduleAdv);
//
//		// Do the protocols
//
//		moduleAdv =
//				mkImplAdvBuiltin(refTcpProtoSpecID,
//				"net.jxta.impl.endpoint.tcp.TcpTransport",
//				"Reference Implementation of the Tcp Proto");
//		protos.put( tcpProtoClassID, moduleAdv);
//
//		moduleAdv =
//				mkImplAdvBuiltin(refHttpProtoSpecID,
//				"net.jxta.impl.endpoint.servlethttp.ServletHttpTransport",
//				"Reference Implementation of the Http Proto");
//		protos.put( httpProtoClassID, moduleAdv);
//
//		// Do the Apps
//
//		moduleAdv =
//				mkImplAdvBuiltin(refStartNetPeerGroupSpecID,
//				"net.jxta.impl.peergroup.StartNetPeerGroup",
//				"Reference Implementation of StartNetPeerGroup");
//		apps.put(applicationClassID, moduleAdv);
//
//		paramAdv.setServices(services);
//		paramAdv.setProtos(protos);
//		paramAdv.setApps(apps);
//
//		// Pour the paramAdv in the platformDef
//		platformDef.setParam((StructuredDocument)paramAdv.getDocument(mimeMediaType));
//
//		return platformDef;
//	}
//
//	

	/**
	 * Constructor for OntoRamaPeerGroup.
	 */
	public OntoRamaPeerGroup() {
		super();
		System.out.println("\nOntoRamaPeerGroup constructor\n");
	}

}
