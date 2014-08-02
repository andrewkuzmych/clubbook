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
#import "UnreadMessages.h"


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

- (void)chat:(NSString *) user_from user_to:(NSString *) user_to msg:(NSString *) msg msg_type:(NSString *) msg_type
{
    [self.communicator chat:user_from user_to:user_to msg:msg msg_type:msg_type];
}

- (void)retrievePlaces:(double) distance lat:(double) lat lon:(double) lon
{
    [self.communicator retrievePlaces:distance lat:lat lon:lon];
}

- (void)retrievePlace:(NSString *) clubId userId:(NSString *) userId
{
    [self.communicator retrievePlace:clubId userId:userId];
}

- (void)retrieveUser:(NSString *) userId
{
    [self.communicator retrieveUser:userId];
}

- (void)retrieveFriend:(NSString *) friendId currnetUserId:(NSString *) currnetUserId
{
    [self.communicator retrieveFriend:friendId currnetUserId:currnetUserId];
}

- (void)retrieveConversation:(NSString *) fromUser toUser:(NSString *) toUser
{
    [self.communicator retrieveConversation:fromUser toUser:toUser];
}

- (void)retrieveConversations:(NSString *) userId
{
    [self.communicator retrieveConversations:userId];
}

- (void)checkin:(NSString *) clubId userId:(NSString *) userId userInfo:(NSObject *) userInfo
{
    [self.communicator checkin:clubId userId:userId userInfo:userInfo];
}

- (void)checkout:(NSString *) clubId userId:(NSString *) userId userInfo:(NSObject *) userInfo
{
    [self.communicator checkout:clubId userId:userId userInfo:userInfo];
}

- (void)updateCheckin:(NSString *) clubId userId:(NSString *) userId
{
    [self.communicator updateCheckin:clubId userId:userId];
}

- (void) readChat:(NSString *) fromUser toUser:(NSString *) toUser
{
    [self.communicator readChat:fromUser toUser:toUser];
}

- (void)unreadMessages:(NSString *) userId
{
    [self.communicator unreadMessages:userId];
}

- (void)addUserImage:(NSString *) userId avatar:(NSString *) avatar
{
    [self.communicator addUserImage:userId avatar:avatar];
}

- (void)updateUserImage:(NSString *) userId objectId:(NSString *) objectId
{
    [self.communicator updateUserImage:userId objectId:objectId];
}

- (void)deleteUserImage:(NSString *) userId objectId:(NSString *) objectId
{
     [self.communicator deleteUserImage:userId objectId:objectId];
}

- (void)updateUser:(NSString *) userId name:(NSString *) name gender:(NSString *) gender  dob:(NSString *) dob country:(NSString *) country bio:(NSString *) bio
{
    [self.communicator updateUser:userId name:name gender:gender dob:dob country:country bio:bio];
}

- (void)sendFriendReguest:(NSString *) userId friendId:(NSString *) friendId
{
    [self.communicator sendFriendRequest:userId friendId:friendId];
}

- (void)confirmFriendRequest:(NSString *) userId friendId:(NSString *) friendId
{
    [self.communicator confirmFriendRequest:userId friendId:friendId];
}

- (void)removeFriend:(NSString *) userId friendId:(NSString *) friendId
{
    [self.communicator removeFriend:userId friendId:friendId];
}

- (void)removeFriendRequest:(NSString *) userId friendId:(NSString *) friendId
{
    [self.communicator removeFriendRequest:userId friendId:friendId];
}

- (void)retrieveFriends:(NSString *) userId
{
   [self.communicator retrieveFriends:userId];
}

- (void)retrievePendingFriends:(NSString *) userId
{
   [self.communicator retrievePendingFriends:userId];
}

- (void)receivedFriendsJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    NSArray *friends = [ObjectBuilder friendsJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didRetrieveFriends:friends];
    }
}

- (void)receivedPendingFriendsJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    NSArray *friends = [ObjectBuilder friendsJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didRetrievePendingFriends:friends];
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
    NSError *localError = nil;
    NSDictionary *parsedObject = [NSJSONSerialization JSONObjectWithData:objectNotation options:0 error:&localError];
    
    if (localError != nil)
    {
//*error = localError;
        //return nil;
    }

    //NSError *error = nil;
    //User *user = [ObjectBuilder checkinFromJSON:objectNotation error:&error];
    
    //if (error != nil) {
    //    [self.delegate  failedWithError:error];
        
    //} else {
    //[self.delegate didReadChat:@"ok"];
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

- (void)unreadMessagesJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    UnreadMessages *unreadMessages = [ObjectBuilder unreadMessagesFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didUnreadMessages:unreadMessages];
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
    NSError *error = nil;
    NSArray *places = [ObjectBuilder placesFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didReceivePlaces:places];
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

- (void)receivedPlaceJSON:(NSData *)objectNotation
{
    NSError *error = nil;
    Place *place = [ObjectBuilder placeFromJSON:objectNotation error:&error];
    
    if (error != nil) {
        [self.delegate  failedWithError:error];
        
    } else {
        [self.delegate didReceivePlace:place];
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

- (void)failedWithError:(NSError *)error
{
    [self.delegate failedWithError:error];
}


@end
