/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2015 DocDoku SARL
 *
 * This file is part of DocDokuPLM.
 *
 * DocDokuPLM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDokuPLM is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
 * GNU Affero General Public License for more details.  
 *  
 * You should have received a copy of the GNU Affero General Public License  
 * along with DocDokuPLM.  If not, see <http://www.gnu.org/licenses/>. 
 */

package com.docdoku.server.rest;

import com.docdoku.core.change.ModificationNotification;
import com.docdoku.core.common.User;
import com.docdoku.core.common.UserGroup;
import com.docdoku.core.configuration.BaselinedFolder;
import com.docdoku.core.configuration.BaselinedPart;
import com.docdoku.core.configuration.DocumentBaseline;
import com.docdoku.core.configuration.ProductBaseline;
import com.docdoku.core.document.DocumentIteration;
import com.docdoku.core.product.*;
import com.docdoku.core.security.ACL;
import com.docdoku.core.security.ACLUserEntry;
import com.docdoku.core.security.ACLUserGroupEntry;
import com.docdoku.server.rest.dto.*;
import com.docdoku.server.rest.dto.baseline.BaselinedDocumentDTO;
import com.docdoku.server.rest.dto.baseline.BaselinedPartDTO;
import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;

import java.util.*;

/**
 *
 * @author Florent Garin
 */
public class Tools {
    
    private Tools(){

    }
    
    public static String stripTrailingSlash(String completePath){
        if(completePath.charAt(completePath.length()-1)=='/') {
            return completePath.substring(0, completePath.length() - 1);
        } else {
            return completePath;
        }
    }
    
    public static String stripLeadingSlash(String completePath){
        if(completePath.charAt(0)=='/') {
            return completePath.substring(1, completePath.length());
        } else {
            return completePath;
        }
    }

    public static DocumentRevisionDTO createLightDocumentRevisionDTO(DocumentRevisionDTO docRsDTO){
       
       if (docRsDTO.getLastIteration() != null) {
           DocumentIterationDTO lastIteration = docRsDTO.getLastIteration();
           List<DocumentIterationDTO> iterations = new ArrayList<>();
           iterations.add(lastIteration);
           docRsDTO.setDocumentIterations(iterations);
       }
       
       docRsDTO.setTags(null);
       docRsDTO.setWorkflow(null);

       return docRsDTO;
   }


    public static ACLDTO mapACLtoACLDTO(ACL acl) {

        ACLDTO aclDTO = new ACLDTO();

        for (Map.Entry<User,ACLUserEntry> entry : acl.getUserEntries().entrySet()) {
            ACLUserEntry aclEntry = entry.getValue();
            aclDTO.addUserEntry(aclEntry.getPrincipalLogin(),aclEntry.getPermission());
        }

        for (Map.Entry<UserGroup,ACLUserGroupEntry> entry : acl.getGroupEntries().entrySet()) {
            ACLUserGroupEntry aclEntry = entry.getValue();
            aclDTO.addGroupEntry(aclEntry.getPrincipalId(),aclEntry.getPermission());
        }

        return aclDTO;

    }

    public static List<ModificationNotificationDTO> mapModificationNotificationsToModificationNotificationDTO(Collection<ModificationNotification> pNotifications){
        List<ModificationNotificationDTO> dtos = new ArrayList<>();
        for(ModificationNotification notification : pNotifications){
            dtos.add(mapModificationNotificationToModificationNotificationDTO(notification));
        }
        return dtos;
    }

    public static ModificationNotificationDTO mapModificationNotificationToModificationNotificationDTO(ModificationNotification pNotification){
        ModificationNotificationDTO dto = new ModificationNotificationDTO();
        Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
        UserDTO userDTO = mapper.map(pNotification.getModifiedPart().getAuthor(),UserDTO.class);
        dto.setAuthor(userDTO);
        dto.setCheckInDate(pNotification.getModifiedPart().getCheckInDate());
        dto.setIterationNote(pNotification.getModifiedPart().getIterationNote());
        dto.setModifiedPartIteration(pNotification.getModifiedPart().getIteration());
        dto.setModifiedPartNumber(pNotification.getModifiedPart().getPartNumber());
        dto.setModifiedPartVersion(pNotification.getModifiedPart().getPartVersion());
        return dto;
    }

    public static PartDTO mapPartRevisionToPartDTO(PartRevision partRevision){

        Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();

        PartDTO partDTO = mapper.map(partRevision,PartDTO.class);

        partDTO.setNumber(partRevision.getPartNumber());
        partDTO.setPartKey(partRevision.getPartNumber() + "-" + partRevision.getVersion());
        partDTO.setName(partRevision.getPartMaster().getName());
        partDTO.setStandardPart(partRevision.getPartMaster().isStandardPart());

        List<PartIterationDTO> partIterationDTOs = new ArrayList<>();
        for(PartIteration partIteration : partRevision.getPartIterations()){
            partIterationDTOs.add(mapPartIterationToPartIterationDTO(partIteration));
        }
        partDTO.setPartIterations(partIterationDTOs);

        if(partRevision.isCheckedOut()){
            partDTO.setCheckOutDate(partRevision.getCheckOutDate());
            UserDTO checkoutUserDTO = mapper.map(partRevision.getCheckOutUser(),UserDTO.class);
            partDTO.setCheckOutUser(checkoutUserDTO);
        }

        if(partRevision.hasWorkflow()){
            partDTO.setLifeCycleState(partRevision.getWorkflow().getLifeCycleState());
            partDTO.setWorkflow(mapper.map(partRevision.getWorkflow(),WorkflowDTO.class));
        }

        ACL acl = partRevision.getACL();
        if(acl != null){
            partDTO.setAcl(Tools.mapACLtoACLDTO(acl));
        }else{
            partDTO.setAcl(null);
        }


        return partDTO;
    }

    public static PartIterationDTO mapPartIterationToPartIterationDTO(PartIteration partIteration){
        Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();

        List<PartUsageLinkDTO> usageLinksDTO = new ArrayList<>();
        PartIterationDTO partIterationDTO = mapper.map(partIteration, PartIterationDTO.class);

        for(PartUsageLink partUsageLink : partIteration.getComponents()){
            PartUsageLinkDTO partUsageLinkDTO = mapper.map(partUsageLink, PartUsageLinkDTO.class);
            List<CADInstanceDTO> cadInstancesDTO = new ArrayList<>();
            for(CADInstance cadInstance : partUsageLink.getCadInstances()){
                CADInstanceDTO cadInstanceDTO = mapper.map(cadInstance,CADInstanceDTO.class);
                cadInstancesDTO.add(cadInstanceDTO);
            }
            List<PartSubstituteLinkDTO> substituteLinkDTOs = new ArrayList<>();
            for(PartSubstituteLink partSubstituteLink:partUsageLink.getSubstitutes()){
                PartSubstituteLinkDTO  substituteLinkDTO = mapper.map(partSubstituteLink,PartSubstituteLinkDTO.class);
                substituteLinkDTOs.add(substituteLinkDTO);

            }
            partUsageLinkDTO.setCadInstances(cadInstancesDTO);
            partUsageLinkDTO.setSubstitutes(substituteLinkDTOs);
            usageLinksDTO.add(partUsageLinkDTO);
        }
        partIterationDTO.setComponents(usageLinksDTO);
        partIterationDTO.setNumber(partIteration.getPartRevision().getPartNumber());
        partIterationDTO.setVersion(partIteration.getPartRevision().getVersion());

        return partIterationDTO;
    }

    public static List<BaselinedPartDTO> mapBaselinedPartsToBaselinedPartDTO(Collection<BaselinedPart> baselinedParts){
        List<BaselinedPartDTO> baselinedPartDTOs = new ArrayList<>();
        for(BaselinedPart baselinedPart : baselinedParts){
            baselinedPartDTOs.add(mapBaselinedPartToBaselinedPartDTO(baselinedPart));
        }
        return baselinedPartDTOs;
    }
    public static List<BaselinedPartDTO> mapBaselinedPartsToBaselinedPartDTO(ProductBaseline productBaseline){
        return mapBaselinedPartsToBaselinedPartDTO(productBaseline.getBaselinedParts().values());
    }
    public static BaselinedPartDTO mapBaselinedPartToBaselinedPartDTO(BaselinedPart baselinedPart){
        return new BaselinedPartDTO(baselinedPart.getTargetPart());
    }

    public static List<BaselinedDocumentDTO> mapBaselinedDocumentsToBaselinedDocumentDTO(Collection<DocumentIteration> baselinedDocuments){
        List<BaselinedDocumentDTO> baselinedDocumentDTOs = new ArrayList<>();
        for(DocumentIteration baselinedDocument : baselinedDocuments){
            baselinedDocumentDTOs.add(mapBaselinedDocumentToBaselinedDocumentDTO(baselinedDocument));
        }
        return baselinedDocumentDTOs;
    }
    public static BaselinedDocumentDTO mapBaselinedDocumentToBaselinedDocumentDTO(DocumentIteration baselineDocument){
        return new BaselinedDocumentDTO(baselineDocument);
    }

    public static List<FolderDTO> mapBaselinedFoldersToFolderDTO(Collection<BaselinedFolder> baselinedFolders){
        List<FolderDTO> folderDTOs = new ArrayList<>();
        for(BaselinedFolder baselinedFolder : baselinedFolders){
            folderDTOs.add(mapBaselinedFolderToFolderDTO(baselinedFolder));
        }
        return folderDTOs;
    }
    public static List<FolderDTO> mapBaselinedFoldersToFolderDTO(DocumentBaseline documentBaseline){
        return mapBaselinedFoldersToFolderDTO(documentBaseline.getBaselinedFolders().values());
    }
    private static FolderDTO mapBaselinedFolderToFolderDTO(BaselinedFolder baselinedFolder) {
        String completePath = baselinedFolder.getCompletePath();
        return new FolderDTO(FolderDTO.extractParentFolder(completePath),FolderDTO.extractName(completePath));
    }
}