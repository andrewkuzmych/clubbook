//
//  BonusClubManager.h
//  BrowseMeetup
//
//  Created by Andrew on 10/2/13.
//  Copyright (c) 2013 TAMIM Ziad. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

#import "ClubbookManagerDelegate.h"
#import "ClubbookCommunicatorDelegate.h"

@class ClubbookCommunicator;


@interface ClubbookManager : NSObject<ClubbookCommunicatorDelegate>

@property (strong, nonatomic) ClubbookCommunicator *communicator;
@property (weak, nonatomic) id<ClubbookManagerDelegate> delegate;

- (void)fbLoginUser:(NSString *) fbId fbAccessToken:(NSString *) fbAccessToken fbAccessExpires:(NSString *) fbAccessExpires gender:(NSString *) gender name:(NSString *) name email:(NSString *) email avatar:(NSString *) avatar bio:(NSString *) bio country:(NSString *) country dob:(NSString *) dob;

- (void)signupUser:(NSString *) name email:(NSString *) email gender:(NSString *) gender city:(NSString *) city pass:(NSString *) pass dob:(NSString *) dob avatar:(NSString *) avatar country:(NSString *) country bio:(NSString *) bio;

- (void)signinUser:(NSString *) email pass:(NSString *) pass;

- (void)chat:(NSString *) user_from user_to:(NSString *) user_to msg:(NSString *) msg msg_type:(NSString *) msg_type accessToken:(NSString *) accessToken;

- (void)retrievePlaces:(double) lat lon:(double) lon take:(int) take skip:(int) skip distance:(int) distance type:(NSString*) type search:(NSString*) search accessToken:(NSString *) accessToken;

- (void)updateUserLocation:(double) lat lon:(double) lon accessToken:(NSString *) accessToken;

- (void)retrievePlace:(NSString *) clubId accessToken:(NSString *) accessToken;

- (void)retrievePlaceUsers:(NSString *) clubId accessToken:(NSString *) accessToken;

- (void)retrievePlaceUsersYesterday:(NSString *) clubId accessToken:(NSString *) accessToken;

- (void)receivedUsers:(BOOL)all gender:(NSString*) gender take:(int) take skip:(int) skip lat:(double) lat lon:(double) lon distance:(double) distance accessToken:(NSString *) accessToken;

- (void)retrieveUser:(NSString *) accessToken;

- (void)retrieveFriend:(NSString *) friendId accessToken:(NSString *) accessToken;

- (void)retrieveConversation:(NSString *) fromUser toUser:(NSString *) toUser accessToken:(NSString *) accessToken;

- (void)retrieveConversations:(NSString *) userId accessToken:(NSString *) accessToken;

- (void)deleteConversation:(NSString *) fromUser toUser:(NSString *) toUser accessToken:(NSString *) accessToken;

- (void)unreadMessages:(NSString *) accessToken;

- (void)readChat:(NSString *) fromUser toUser:(NSString *) toUser accessToken:(NSString *) accessToken;

- (void)retrieveFriends:(NSString *) userId accessToken:(NSString *) accessToken;

- (void)retrievePendingFriends:(NSString *) userId accessToken:(NSString *) accessToken;

- (void)checkin:(NSString *) clubId accessToken:(NSString *) accessToken userInfo:(NSObject *) userInfo;

- (void)checkout:(NSString *) clubId accessToken:(NSString *) accessToken userInfo:(NSObject *) userInfo;

- (void)updateCheckin:(NSString *) clubId accessToken:(NSString *) accessToken;

- (void)addUserImage:(NSString *) userId avatar:(NSString *) avatar accessToken:(NSString *) accessToken;

- (void)updateUserImage:(NSString *) userId objectId:(NSString *) objectId accessToken:(NSString *) accessToken;

- (void)deleteUserImage:(NSString *) userId objectId:(NSString *) objectId accessToken:(NSString *) accessToken;

- (void)changeUserVisible:(NSString*) accessToken visible:(BOOL) visible;

- (void)changeUserPush:(NSString *) accessToken push:(BOOL) push;

- (void)updateUser:(NSString *) accessToken name:(NSString *) name gender:(NSString *) gender  dob:(NSString *) dob country:(NSString *) country bio:(NSString *) bio;

- (void)deleteUser:(NSString *) accessToken;

- (void)sendFriendReguest:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken;

- (void)confirmFriendRequest:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken;

- (void)removeFriend:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken;

- (void)removeFriendRequest:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken;

- (void)changePass:(NSString *) oldPass newPass:(NSString *) newPass accessToken:(NSString *) accessToken;

- (void)blockUser:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken;

- (void)unblockUser:(NSString *) userId friendId:(NSString *) friendId accessToken:(NSString *) accessToken;

- (void)inviteFbFriends:(NSString *) userId fb_ids:(NSArray *) fb_ids accessToken:(NSString *) accessToken;

- (void)findFbFriends:(NSArray *) fb_ids accessToken:(NSString *) accessToken;

- (void) getConfig;

@end
