//
//  BonusClubManager.m
//  BrowseMeetup
//
//  Created by Andrew on 10/2/13.
//  Copyright (c) 2013 TAMIM Ziad. All rights reserved.
//

#import "ClubbookManager.h"

#import "ObjectBuilder.h"
#import "ClubbookCommunicator.h"
#import "Chat.h"
#import "ErrorViewController.h"
#import "ClubbookNotifications.h"
#import "Config.h"
#import "UsersYesterday.h"


@implementation ClubbookManager


- (void)fbLoginUser:(NSString *) fbId fbAccessToken:(NSString *) fbAccessToken fbAccessExpires:(NSString *) fbAccessExpires gender:(NSString *) gender name:(NSString *) name email:(NSString *) email avatar:(NSString *) avatar bio:(NSString *) bio country:(NSString *) country dob:(NSString *) dob
{
    [self.communicator fbLoginUser:fbId fbAccessToken:fbAccessToken fbAccessExpires:fbAccessExpires gender:gender name:name email:email avatar:avatar bio:bio country:country dob:dob];
}

- (void)signupUser:(NSString *) name email:(NSString *) email gender:(NSString *) gender city:(NSString *) city pass:(NSString *) pass dob:(NSString *) dob avatar:(NSString *) avatar country:(NSString *) country bio:(NSString *) bio;
{
    [self.communicator signupUser:name email:email gender:gender city:city pass:pass dob:dob avatar:avatar country:country bio:bio];
}

- (void)signinUser:(NSString *) email pass:(NSString *) pass
{
     [self.communicator signinUser:email pass:pass];
}

- (void)chat:(NSString *) user_from user_to:(NSString *) user_to msg:(NSString *) msg msg_type:(NSString *) msg_type url:(NSString*) url location:(NSDictionary*) location accessToken:(NSString *) accessToken
{
    [self.communicator chat:user_from user_to:user_to msg:msg msg_type:msg_type url:url location:location accessToken:accessToken];
}

- (void)retrievePlaces:(double) lat lon:(double) lon take:(int) take skip:(int) skip distance:(int) distance type:(NSString*) type search:(NSString*) search accessToken:(NSString *) accessToken;
{
    [self.communicator retrievePlaces:lat lon:lon take:take skip:skip distance:distance type:type search:search accessToken:accessToken];
}

- (void) retrieveYesterdayPlacesAccessToken:(NSString*) accessToken {
    [self.communicator retrieveYesterdayPlacesAccessToken:accessToken];
}

- (void)retrievePlace:(NSString *) clubId accessToken:(NSString *) accessToken
{
    [self.communicator retrievePlace:clubId accessToken:accessToken];
}

- (void)makePlaceFavorite:(NSString*) clubId accessToken:(NSString *)accessToken makeFavorite:(BOOL) makeFavorite {
    [self.communicator makePlaceFavorite:clubId accessToken:accessToken makeFavorite:makeFavorite];
}

- (void)retrievePlaceUsers:(NSString *) clubId accessToken:(NSString *) accessToken
{
    [self.communicator retrievePlaceUsers:clubId accessToken:accessToken];
}

- (void)retrievePlaceUsersYesterday:(NSString *) clubId accessToken:(NSString *) accessToken
{
    [self.communicator retrievePlaceUsersYesterday:clubId accessToken:accessToken];
}

- (void)retrieveUser:(NSString *) accessToken
{
    [self.communicator retrieveUser:accessToken];
}

- (void)retrieveNews:(NSString*)type withId:(NSString*)objectId accessToken:(NSString*) accessToken skip:(int)skip limit:(int)limit{
    [self.communicator retrieveNews:type withId:objectId accessToken:accessToken skip:skip limit:limit];
}

- (void)retrieveFriend:(NSString *) friendId accessToken:(NSString *) accessToken
{
    [self.communicator retrieveFriend:friendId accessToken:accessToken];
}

- (void)retrieveConversation:(NSString *) fromUser toUser:(NSString *) toUser accessToken:(NSString *) accessToken
{
    [self.communicator retrieveConversation:fromUser toUser:toUser accessToken:accessToken];
}

- (void)retrieveConversations:(NSString *) userId accessToken:(NSString *) accessToken
{
    [self.communicator retrieveConversations:userId accessToken:accessToken];
}

- (void)deleteConversation:(NSString *) fromUser toUser:(NSString *) toUser accessToken:(NSString *) accessToken
{
    [self.communicator deleteConversation:fromUser toUser:toUser accessToken:accessToken];
}

- (void)checkin:(NSString *) clubId accessToken:(NSString *) accessToken userInfo:(NSObject *) userInfo
{
    [self.communicator checkin:clubId accessToken:accessToken userInfo:userInfo];
}

- (void)checkout:(NSString *) clubId accessToken:(NSString *) accessToken userInfo:(NSObject *) userInfo
{
    [self.communicator checkout:clubId accessToken:accessToken userInfo:userInfo];
}

- (void)updateCheckin:(NSString *) clubId accessToken:(NSString *) accessToken
{
    [self.communicator updateCheckin:clubId accessToken:accessToken];
}

- (void) readChat:(NSString *) fromUser toUser:(NSString *) toUser accessToken:(NSString *) accessToken
{
    [self.communicator readChat:fromUser toUser:toUser accessToken: accessToken];
}

- (void)retrieveNotifications:(double) lat lon:(double) lon accessToken:(NSString *)accessToken
{
    [self.communicator retrieveNotifications:lat lon:lon accessToken:accessToken];
}

- (void)addUserImage:(NSString *) userId avatar:(NSString *) avatar accessToken:(NSString *) accessToken
{
    [self.communicator addUserImage:userId avatar:avatar accessToken:accessToken];
}

- (void)changeUserVisible:(NSString*) accessToken visible:(BOOL) visible
{
    [self.communicator changeUserVisibilityNearby:accessToken isVisible:visible];
}

- (void)changeUserPush:(NSString *) accessToken push:(BOOL) push
{
    [self.communicator changeUserPush:accessToken push:push];
}

- (void)updateUserImage:(NSString *) userId objectId:(NSString *) objectId accessToken:(NSString *) accessToken
{
    [self.communicator updateUserImage:userId objectId:objectId accessToken:accessToken];
}

- (void)deleteUserImage:(NSString *) userId objectId:(NSString *) objectId accessToken:(NSString *) accessToken
{
     [self.communicator deleteUserImage:userId objectId:objectId accessToken:accessToken];
}

- (void)updateUser:(NSString *) accessToken name:(NSString *) name gender:(NSString *) gender  dob:(NSString *) dob country:(NSString *) country bio:(NSString *) bio
{
    [self.communicator updateUser:accessToken name:name gender:gender dob:dob country:country bio:bio];
}

- (void)deleteUser:(NSString *) accessToken
{
    [self.communicator deleteUser:accessToken];
}

- (void)sendFriendReguest:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken
{
    [self.communicator sendFriendRequest:userId friendId:friendId accessToken:accessToken];
}

- (void)confirmFriendRequest:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken
{
    [self.communicator confirmFriendRequest:userId friendId:friendId accessToken:accessToken];
}

- (void)removeFriend:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken
{
    [self.communicator removeFriend:userId friendId:friendId accessToken:accessToken];
}

- (void)updateUserLocation:(double) lat lon:(double) lon accessToken:(NSString *) accessToken
{
    [self.communicator updateUserLocation:lat lon:lon accessToken:accessToken];
}

- (void)removeFriendRequest:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken
{
    [self.communicator removeFriendRequest:userId friendId:friendId accessToken:accessToken];
}

- (void)receivedUsers:(BOOL)all gender:(NSString*) gender take:(int) take skip:(int) skip lat:(double) lat lon:(double) lon distance:(double) distance accessToken:(NSString *) accessToken
{
    [self.communicator receivedUsers:all gender:gender take:take skip:skip lat:lat lon:lon distance:distance accessToken:accessToken];
}

- (void)retrieveFriends:(NSString *) userId accessToken:(NSString *) accessToken
{
    [self.communicator retrieveFriends:userId accessToken:accessToken];
}

- (void)retrievePendingFriends:(NSString *) userId accessToken:(NSString *) accessToken
{
    [self.communicator retrievePendingFriends:userId accessToken:accessToken];
}

- (void)changePass:(NSString *) oldPass newPass:(NSString *) newPass accessToken:(NSString *) accessToken
{
    [self.communicator changePass:oldPass newPass:newPass accessToken:accessToken];
}

- (void)blockUser:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken;
{
    [self.communicator blockUser:userId friendId:friendId accessToken:accessToken];
}

- (void)unblockUser:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken
{
    [self.communicator unblockUser:userId friendId:friendId accessToken:accessToken];
}

- (void)inviteFbFriends:(NSString *) userId fb_ids:(NSArray *) fb_ids accessToken:(NSString *) accessToken
{
    [self.communicator inviteFbFriends:userId fb_ids:fb_ids accessToken:accessToken];
}

- (void)findFbFriends:(NSArray *) fb_ids accessToken:(NSString *) accessToken
{
    [self.communicator findFbFriends:fb_ids accessToken:accessToken];
}

- (void)getConfig
{
    [self.communicator getConfig];
}

- (void)getConfigJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    Config *config = [ObjectBuilder getConfigFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didGetConfig:config];
    }
}

- (void)deleteConversationJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    //User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didDeleteConversation:@"ok"];
    }
}

- (void)receivedFriendsJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    FriendsResult *friendsResult = [ObjectBuilder friendsJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didRetrieveFriends:friendsResult];
    }
}

- (void)changeUserVisibleNearbyJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didChangeVisibleNearby:user];
    }
}

- (void)changeUserPushJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didChangePush:user];
    }
}

- (void)receivedPendingFriendsJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    FriendsResult *friendsResult  = [ObjectBuilder friendsJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didRetrievePendingFriends:friendsResult];
    }
}

- (void)updateUserLocationJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didUpdateUserLocation:@"ok"];
    }
}
- (void)changePassJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    BOOL result = [ObjectBuilder changePassFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didChangePass:result];
    }
    
}

- (void)sendFriendJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder tempUserFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didSendFriend:user];
    }

}

- (void)confirmFriendJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder tempUserFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didConfirmFriend:user];
    }
}

- (void)removeFriendJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder tempUserFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didRemoveFriend:user];
    }
}

- (void)removeFriendRequestJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder tempUserFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didRemoveFriendRequest:user];
    }
}

- (void)updateUserJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didUpdateUser:user];
    }
}

- (void)deleteUserJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    //User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didDeleteUser:@"ok"];
    }
}

- (void)deleteUserImageJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didDeleteUserImage:user];
    }
}

- (void)updateUserImageJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didUpdateUserImage:user];
    }
}

- (void)addUserImageJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didAddUserImage:user];
    }
}

- (void)readChatJSON:(NSData *)objectNotation
{
    
}

- (void)updateCheckinJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder checkinFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didUpdateCheckin:user];
    }
}

- (void)notificationsJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    ClubbookNotifications *notifications = [ObjectBuilder unreadMessagesFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didReceivedNotifications:notifications];
    }
}

- (void)checkinJSON:(NSData *)objectNotation userInfo:(NSObject *) userInfo
{
    NSError *error = nil;
    User *user = [ObjectBuilder checkinFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
    } else {
        [self.delegate didCheckin:user userInfo:userInfo];
    }
}

- (void)checkoutJSON:(NSData *)objectNotation userInfo:(NSObject *) userInfo
{
    NSError *error = nil;
    User *user = [ObjectBuilder checkinFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
    } else {
        [self.delegate didCheckout:user userInfo:userInfo];
    }
}

- (void)receivedPlacesJSON:(NSData *)objectNotation
{
    NSError *errorPlaces = nil;
    NSError *errorTypes = nil;
    NSArray *places = [ObjectBuilder placesFromJSON:objectNotation error:&errorPlaces];
    NSArray *types = [ObjectBuilder typesFromJSON:objectNotation error:&errorTypes];
    
    if (errorPlaces != nil) {
        [self.delegate  failedWithError:errorPlaces];
        
    } else if (errorTypes != nil) {
        [self.delegate  failedWithError:errorTypes];
    }
    else {
        [self.delegate didReceivePlaces:places andTypes:types];
    }
}

- (void)receivedConversationJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    Chat *chat = [ObjectBuilder chatFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didReceiveConversation:chat];
    }
}


- (void)receivedConversationsJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    NSArray *chats = [ObjectBuilder conversationsJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didReceiveConversations:chats];
    }
}


- (void)receivedUserJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didReceiveUser:user];
    }
}

- (void)receivedFriendJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder userFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didReceiveFriend:user];
    }
}

- (void)receivedNewsJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    NSArray *news = [ObjectBuilder newsFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didReceiveNews:news];
    }
}

- (void)receivedPlaceUsersJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    NSArray *users = [ObjectBuilder placeUsersFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didReceivePlaceUsers:users];
    }
}

- (void)receivedUsersCheckedinJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    NSArray *users = [ObjectBuilder placeUsersFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didReceiveUsersCheckedin:users];
    }
}

- (void)receivedPlaceUsersYesterdayJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    UsersYesterday *usersYesterday = [ObjectBuilder placeUsersYesterdayFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didReceivePlaceUsersYesterday:usersYesterday];
    }
}

- (void)signupJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder signupFromJSON:objectNotation error:&error];

    if (error != nil) {
        [self.delegate  failedWithError:error];

    } else {
        [self.delegate didSignapUser:user];
    }
}

- (void)chatJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    //TODO: parse resondse
    //User *user = [ObjectBuilder signupFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didChat:@"ok"];
    }
}

- (void)signinJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder signinFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didSigninUser:user];
    }
}

- (void)fbLoginJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    User *user = [ObjectBuilder fbLoginFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didFbLoginUser:user];
    }
}

- (void)blockUserJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    //User *user = [ObjectBuilder fbLoginFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didBlockUser:@"ok"];
    }
}

- (void)unblockUserJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    //User *user = [ObjectBuilder fbLoginFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didUnblockUser:@"ok"];
    }
}

- (void)inviteFbFriendsJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    //User *user = [ObjectBuilder fbLoginFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didInviteFbFriends:@"ok"];
    }
}

- (void)findFbFriendsJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    NSArray *users = [ObjectBuilder usersJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didFindFbFriends:users];
    }
}

- (void)failedWithError:(NSError *)error
{
    [self.delegate failedWithError:error];
}


@end
