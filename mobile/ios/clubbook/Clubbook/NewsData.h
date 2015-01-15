//
//  NewsData.h
//  Clubbook
//
//  Created by Anton Semenyuk on 1/14/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NewsData : NSObject

@property (strong, nonatomic) NSString* nameUser;
@property (strong, nonatomic) NSDate* dateOfPost;
@property (strong, nonatomic) UIImage* avatarImageUrl;
@property (strong, nonatomic) NSString* messageText;
@property (strong, nonatomic) NSMutableArray* arrayOfPhotos;

@end
