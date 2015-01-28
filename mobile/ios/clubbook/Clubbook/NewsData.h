//
//  NewsData.h
//  Clubbook
//
//  Created by Anton Semenyuk on 1/14/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NewsData : NSObject

@property (strong, nonatomic) NSString* createdById;
@property (strong, nonatomic) NSDate* createDate;
@property (strong, nonatomic) NSString* newsDescription;
@property (strong, nonatomic) NSString* title;
@property (strong, nonatomic) NSString* avatarPath;
@property (strong, nonatomic) NSMutableArray* photos;
@property (strong, nonatomic) NSMutableDictionary* tempDownlaodedPhotos;

@property (strong, nonatomic) NSString* type;

@property (strong, nonatomic) NSDate* startTime;
@property (strong, nonatomic) NSDate* endTime;
@property (strong, nonatomic) NSString* shareLink;
@property (strong, nonatomic) NSString* buyLink;

@end
